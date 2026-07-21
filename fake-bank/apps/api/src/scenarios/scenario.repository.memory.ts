import type { ScenarioExecutionRepository } from './scenario.repository.js';
import type {
  CompleteScenarioExecutionStateInput,
  CreateScenarioExecutionStateInput,
  ScenarioExecutionState,
} from './scenario.types.js';

export function createMemoryScenarioExecutionRepository(): ScenarioExecutionRepository {
  const states = new Map<string, ScenarioExecutionState>();

  return {
    createScenarioState(
      input: CreateScenarioExecutionStateInput,
    ): Promise<ScenarioExecutionState> {
      const existing = [...states.values()].find((state) => state.payment_id === input.payment_id);

      if (existing) {
        return Promise.resolve(existing);
      }

      const now = new Date().toISOString();
      const state: ScenarioExecutionState = {
        ...input,
        created_at: now,
        updated_at: now,
      };

      states.set(state.scenario_state_id, state);
      return Promise.resolve(state);
    },

    completeScenarioState(
      input: CompleteScenarioExecutionStateInput,
    ): Promise<ScenarioExecutionState> {
      const state = states.get(input.scenario_state_id);

      if (!state) {
        throw new Error(`Scenario state was not found: ${input.scenario_state_id}`);
      }

      const now = new Date().toISOString();
      const completed: ScenarioExecutionState = {
        ...state,
        current_step: input.current_step,
        completed_at: now,
        updated_at: now,
      };

      states.set(completed.scenario_state_id, completed);
      return Promise.resolve(completed);
    },

    findScenarioStateByPaymentId(paymentId: string): Promise<ScenarioExecutionState | null> {
      const state = [...states.values()].find((candidate) => candidate.payment_id === paymentId);
      return Promise.resolve(state ?? null);
    },
  };
}
