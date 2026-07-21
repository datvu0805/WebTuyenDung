import type {
  CompleteScenarioExecutionStateInput,
  CreateScenarioExecutionStateInput,
  ScenarioExecutionState,
} from './scenario.types.js';

export type ScenarioExecutionRepository = {
  createScenarioState(
    input: CreateScenarioExecutionStateInput,
  ): Promise<ScenarioExecutionState>;
  completeScenarioState(
    input: CompleteScenarioExecutionStateInput,
  ): Promise<ScenarioExecutionState>;
  findScenarioStateByPaymentId(paymentId: string): Promise<ScenarioExecutionState | null>;
};
