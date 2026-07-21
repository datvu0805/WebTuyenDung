import { paymentMethods, paymentScenarios, paymentStatuses } from './payment.types.js';

const paymentIdSchema = {
  type: 'string',
  pattern: '^pay_[A-Za-z0-9]+$',
};

const amountSchema = {
  type: 'number',
  exclusiveMinimum: 0,
};

const currencySchema = {
  type: 'string',
  minLength: 3,
  maxLength: 3,
  pattern: '^[A-Z]{3}$',
};

const metadataSchema = {
  type: 'object',
  additionalProperties: { type: 'string' },
};

export const createPaymentBodySchema = {
  type: 'object',
  additionalProperties: false,
  required: ['merchant_id', 'amount', 'currency', 'payment_method', 'return_url', 'webhook_url'],
  properties: {
    merchant_id: { type: 'string', minLength: 1, maxLength: 100 },
    merchant_reference: { type: 'string', maxLength: 200 },
    amount: amountSchema,
    currency: currencySchema,
    payment_method: { type: 'string', enum: paymentMethods },
    return_url: { type: 'string', format: 'uri' },
    webhook_url: { type: 'string', format: 'uri' },
    scenario: { type: 'string', enum: paymentScenarios },
    metadata: metadataSchema,
  },
} as const;

export const createPaymentResponseSchema = {
  type: 'object',
  additionalProperties: false,
  required: ['payment_id', 'status', 'checkout_url'],
  properties: {
    payment_id: paymentIdSchema,
    status: { type: 'string', enum: paymentStatuses },
    checkout_url: { type: 'string', format: 'uri' },
  },
} as const;

export const paymentParamsSchema = {
  type: 'object',
  additionalProperties: false,
  required: ['payment_id'],
  properties: {
    payment_id: paymentIdSchema,
  },
} as const;

export const paymentResponseSchema = {
  type: 'object',
  additionalProperties: false,
  required: [
    'payment_id',
    'merchant_id',
    'amount',
    'currency',
    'payment_method',
    'status',
    'checkout_url',
    'return_url',
    'webhook_url',
    'created_at',
    'updated_at',
  ],
  properties: {
    payment_id: paymentIdSchema,
    merchant_id: { type: 'string' },
    merchant_reference: { type: 'string' },
    amount: amountSchema,
    currency: currencySchema,
    payment_method: { type: 'string', enum: paymentMethods },
    status: { type: 'string', enum: paymentStatuses },
    checkout_url: { type: 'string', format: 'uri' },
    return_url: { type: 'string', format: 'uri' },
    webhook_url: { type: 'string', format: 'uri' },
    scenario: { type: 'string', enum: paymentScenarios },
    metadata: metadataSchema,
    created_at: { type: 'string', format: 'date-time' },
    updated_at: { type: 'string', format: 'date-time' },
  },
} as const;

export const confirmPaymentBodySchema = {
  type: 'object',
  additionalProperties: false,
  required: ['payment_method', 'card'],
  properties: {
    payment_method: { type: 'string', enum: paymentMethods },
    card: {
      type: 'object',
      additionalProperties: false,
      required: ['number', 'expiry', 'cvv'],
      properties: {
        number: { type: 'string', pattern: '^[0-9]{12,19}$' },
        expiry: { type: 'string', pattern: '^(0[1-9]|1[0-2])/[0-9]{2}$' },
        cvv: { type: 'string', pattern: '^[0-9]{3,4}$' },
      },
    },
  },
} as const;

export const errorResponseSchema = {
  type: 'object',
  additionalProperties: false,
  required: ['error_code', 'message', 'request_id'],
  properties: {
    error_code: { type: 'string' },
    message: { type: 'string' },
    request_id: { type: 'string' },
    details: { type: 'object', additionalProperties: true },
  },
} as const;
