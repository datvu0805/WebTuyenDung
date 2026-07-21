export type CardSimulationResult = 'success' | 'declined';

export class InvalidTestCardError extends Error {
  constructor() {
    super('Unsupported test card.');
  }
}

export type TestCardInput = {
  number: string;
  expiry: string;
  cvv: string;
};

export function resolveTestCard(input: TestCardInput): CardSimulationResult {
  if (input.number === '4242424242424242') {
    return 'success';
  }

  if (input.number === '4000000000000002') {
    return 'declined';
  }

  throw new InvalidTestCardError();
}
