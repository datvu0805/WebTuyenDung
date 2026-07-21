import type { QueryResultRow } from 'pg';
import type { DbPool } from '../server/db.js';
import type { ScenarioExecutionRepository } from './scenario.repository.js';
import type {
  CompleteScenarioExecutionStateInput,
  CreateScenarioExecutionStateInput,
  ScenarioExecutionState,
} from './scenario.types.js';

type ScenarioExecutionStateRow = QueryResultRow & {
  scenario_state_id: string;
  payment_id: string;
  scenario: ScenarioExecutionState['scenario'];
  current_step: string;
  scheduled_at: Date | null;
  completed_at: Date | null;
  created_at: Date;
  updated_at: Date;
};

export function createPostgresScenarioExecutionRepository(
  pool: DbPool,
): ScenarioExecutionRepository {
  return {
    createScenarioState(
      input: CreateScenarioExecutionStateInput,
    ): Promise<ScenarioExecutionState> {
      return pool
        .query<ScenarioExecutionStateRow>(
          `
            INSERT INTO scenario_execution_state (
              scenario_state_id,
              payment_id,
              scenario,
              current_step,
              scheduled_at
            )
            VALUES ($1, $2, $3, $4, $5)
            ON CONFLICT (payment_id) DO UPDATE
            SET
              current_step = EXCLUDED.current_step,
              scheduled_at = EXCLUDED.scheduled_at,
              updated_at = now()
            RETURNING *
          `,
          [
            input.scenario_state_id,
            input.payment_id,
            input.scenario,
            input.current_step,
            input.scheduled_at ?? null,
          ],
        )
        .then((result) => toScenarioExecutionState(result.rows[0]));
    },

    completeScenarioState(
      input: CompleteScenarioExecutionStateInput,
    ): Promise<ScenarioExecutionState> {
      return pool
        .query<ScenarioExecutionStateRow>(
          `
            UPDATE scenario_execution_state
            SET
              current_step = $2,
              completed_at = now(),
              updated_at = now()
            WHERE scenario_state_id = $1
            RETURNING *
          `,
          [input.scenario_state_id, input.current_step],
        )
        .then((result) => toScenarioExecutionState(result.rows[0]));
    },

    findScenarioStateByPaymentId(paymentId: string): Promise<ScenarioExecutionState | null> {
      return pool
        .query<ScenarioExecutionStateRow>(
          `
            SELECT *
            FROM scenario_execution_state
            WHERE payment_id = $1
          `,
          [paymentId],
        )
        .then((result) => {
          return result.rows[0] ? toScenarioExecutionState(result.rows[0]) : null;
        });
    },
  };
}

function toScenarioExecutionState(
  row: ScenarioExecutionStateRow | undefined,
): ScenarioExecutionState {
  if (!row) {
    throw new Error('Scenario execution state row was not returned.');
  }

  const state: ScenarioExecutionState = {
    scenario_state_id: row.scenario_state_id,
    payment_id: row.payment_id,
    scenario: row.scenario,
    current_step: row.current_step,
    created_at: row.created_at.toISOString(),
    updated_at: row.updated_at.toISOString(),
  };

  if (row.scheduled_at !== null) {
    state.scheduled_at = row.scheduled_at.toISOString();
  }

  if (row.completed_at !== null) {
    state.completed_at = row.completed_at.toISOString();
  }

  return state;
}
