import api from './axios';

export const authApi = {
  login: (username, password) =>
    api.post('/login', new URLSearchParams({ username, password })),

  registerCandidate: (formData) =>
    api.post('/register-candidate', formData, {
      headers: { 'Content-Type': 'multipart/form-data' },
    }),

  registerEmployer: (data) =>
    api.post('/register-employer', new URLSearchParams(data)),
};

export const jobApi = {
  getAll: () => api.get('/jobs'),
  getById: (id) => api.get(`/jobs/${id}`),
  search: (params) => api.get('/jobs/search', { params }),
  getByCompany: (companyId) => api.get('/jobs/company', { params: { companyId } }),
  getRecommended: (limit = 10) => api.get('/jobs/recommended', { params: { limit } }),
  create: (data) => api.post('/jobs', new URLSearchParams(data)),
  update: (data) => api.put('/jobs', new URLSearchParams(data)),
  delete: (id) => api.delete(`/jobs?id=${id}`),
  import: (formData) => api.post('/jobs/import', formData, { headers: { 'Content-Type': 'multipart/form-data' } }),
};

export const skillApi = {
  getAll: () => api.get('/skill'),
  create: (skillName) => api.post('/skill', new URLSearchParams({ skillName })),
  update: (id, skillName) => api.put('/skill', new URLSearchParams({ id, skillName })),
  delete: (id) => api.delete(`/skill?id=${id}`),
};

export const jobSkillApi = {
  getByJob: (jobId) => api.get(`/job-skills?jobId=${jobId}`),
  add: (jobId, skillId) =>
    api.post('/job-skills', new URLSearchParams({ jobId, skillId })),
  remove: (jobId, skillId) =>
    api.delete(`/job-skills?jobId=${jobId}&skillId=${skillId}`),
};

export const candidateSkillApi = {
  getByCandidate: (candidateId) => api.get(`/candidate-skills/candidate?id=${candidateId}`),
  replaceBatch: (skillIds) =>
    api.put('/candidate-skills/batch', { skillIds }, { headers: { 'Content-Type': 'application/json' } }),
};

export const favoriteJobApi = {
  getByCandidate: (candidateId) => api.get(`/favorite-jobs/candidate?id=${candidateId}`),
  check: (candidateId, jobId) => api.get('/favorite-jobs/check', { params: { candidateId, jobId } }),
  add: (jobId) => api.post('/favorite-jobs', { jobId }, { headers: { 'Content-Type': 'application/json' } }),
  remove: (jobId) => api.delete('/favorite-jobs', { data: { jobId }, headers: { 'Content-Type': 'application/json' } }),
};

export const cvApi = {
  upload: (formData) =>
    api.post('/UploadCV', formData, {
      headers: { 'Content-Type': 'multipart/form-data' },
    }),
  getByCandidate: (candidateId) => api.get(`/quanLyCV?candidate_id=${candidateId}`),
  delete: (cvId, candidateId) =>
    api.post('/XoaCV', new URLSearchParams({ cv_id: cvId, candidate_id: candidateId })),
};

export const candidateApi = {
  getProfile: () => api.get('/candidate/profile'),
  getAll: () => api.get('/candidate/list'),
  updateProfile: (data) => api.put('/candidate/profile', data, { headers: { 'Content-Type': 'application/json' } }),
  uploadAvatar: (file) => {
    const form = new FormData();
    form.append('avatar', file);
    return api.post('/user/avatar', form, { headers: { 'Content-Type': 'multipart/form-data' } });
  },
};

export const employerApi = {
  getProfile: () => api.get('/employer/profile'),
  updateProfile: (data) => api.put('/employer/profile', new URLSearchParams(data)),
  uploadAvatar: (file) => {
    const form = new FormData();
    form.append('avatar', file);
    return api.post('/user/avatar', form, { headers: { 'Content-Type': 'multipart/form-data' } });
  },
};

export const applicationApi = {
  submit: (data) =>
    api.post('/api/aplication', new URLSearchParams({ ...data, action: 'submit' })),
  updateStatus: (id, status) =>
    api.post(
      '/api/aplication',
      new URLSearchParams({ id, status, action: 'updateStatus' })
    ),
  getRecruiterList: () => api.get('/api/aplication?action=viewList'),
};

export const paymentApi = {
  getPackages: () => api.get('/api/payment/packages'),
  getVipStatus: () => api.get('/api/payment/vip-status'),
  createPayment: (packageID) =>
    api.post('/api/payment/create', { packageID }, {
      headers: { 'Content-Type': 'application/json' },
    }),
  getTransactionStatus: (txnRef) =>
    api.get('/api/payment/transaction-status', { params: { txnRef } }),
  getHistory: () => api.get('/api/payment/history'),
};

export const certificateApi = {
  getAll: () => api.get('/certificate'),
  getById: (id) => api.get(`/certificate/${id}`),
  create: (data) => api.post('/certificate', new URLSearchParams(data)),
  update: (data) => api.put('/certificate', new URLSearchParams(data)),
  delete: (id) => api.delete(`/certificate?id=${id}`),
};

export const messageApi = {
  getHistory: (withUserId) => api.get('/api/messages', { params: { action: 'history', withUserId } }),
  getRecent: () => api.get('/api/messages', { params: { action: 'recent' } }),
  getUnread: () => api.get('/api/messages', { params: { action: 'unread' } }),
};

export const candidateCertificateApi = {
  getAll: () => api.get('/candidate-certificate'),
  getById: (id) => api.get(`/candidate-certificate/${id}`),
  create: (data) => api.post('/candidate-certificate', new URLSearchParams(data)),
  update: (data) => api.put('/candidate-certificate', new URLSearchParams(data)),
  delete: (id) => api.delete(`/candidate-certificate?id=${id}`),
};

export const adminCompanyApi = {
  getAll: () => api.get('/admin/company/list'),
  create: (data) => api.post('/admin/company/create', data, { headers: { 'Content-Type': 'application/json' } }),
  update: (data) => api.put('/admin/company/update', data, { headers: { 'Content-Type': 'application/json' } }),
  delete: (id) => api.delete(`/admin/company/delete?id=${id}`),
  import: (formData) => api.post('/admin/company/import', formData, { headers: { 'Content-Type': 'multipart/form-data' } }),
};

export const adminSettingsApi = {
  getAll: () => api.get('/admin/settings'),
  update: (key, value) => api.put('/admin/settings', { key, value }, { headers: { 'Content-Type': 'application/json' } }),
};

export const adminStatisticApi = {
  get: (params = {}) => api.get('/admin/statistic', { params }),
  getReport: (report, params = {}) => api.get('/admin/statistic', { params: { ...params, report } }),
  export: (format, params = {}) => api.get('/admin/statistic/export', {
    params: { ...params, format },
    responseType: 'blob',
  }),
};

export const jobPositionApi = {
  getAll: () => api.get('/admin/job-positions'),
  create: (data) => api.post('/admin/job-positions', data, { headers: { 'Content-Type': 'application/json' } }),
  update: (data) => api.put('/admin/job-positions', data, { headers: { 'Content-Type': 'application/json' } }),
  delete: (id) => api.delete(`/admin/job-positions?id=${id}`),
  import: (formData) => api.post('/admin/job-positions/import', formData, { headers: { 'Content-Type': 'multipart/form-data' } }),
};

export const educationLevelApi = {
  getAll: () => api.get('/education-level'),
  create: (levelName) => api.post('/education-level', { levelName }, { headers: { 'Content-Type': 'application/json' } }),
  update: (id, levelName) => api.put('/education-level', { id, levelName }, { headers: { 'Content-Type': 'application/json' } }),
  delete: (id) => api.delete(`/education-level?id=${id}`),
};

export const candidateEducationApi = {
  getByCandidate: (candidateId) => api.get(`/candidate-education/candidate?id=${candidateId}`),
  create: (data) => api.post('/candidate-education', data, { headers: { 'Content-Type': 'application/json' } }),
  update: (data) => api.put('/candidate-education', data, { headers: { 'Content-Type': 'application/json' } }),
  delete: (id) => api.delete(`/candidate-education?id=${id}`),
};

export const jobEducationApi = {
  getByJob: (jobId) => api.get(`/job-educations/job?id=${jobId}`),
  add: (jobId, educationLevelId) =>
    api.post('/job-educations', { jobId, educationLevelId }, { headers: { 'Content-Type': 'application/json' } }),
  remove: (jobId, educationLevelId) =>
    api.delete('/job-educations', { data: { jobId, educationLevelId }, headers: { 'Content-Type': 'application/json' } }),
};

export const jobCertificateApi = {
  getByJob: (jobId) => api.get(`/job-certificates/job?id=${jobId}`),
  add: (jobId, certificateId, requiredScore) =>
    api.post('/job-certificates/single', { jobId, certificateId, requiredScore }, { headers: { 'Content-Type': 'application/json' } }),
  remove: (jobId, certificateId) =>
    api.delete('/job-certificates', { data: { jobId, certificateId }, headers: { 'Content-Type': 'application/json' } }),
};

export const cvCertificateApi = {
  getByCv: (cvId) => api.get(`/cv-certificates?cvId=${cvId}`),
  replaceForCv: (cvId, candidateCertificateIds) =>
    api.put('/cv-certificates', { cvId, candidateCertificateIds }, { headers: { 'Content-Type': 'application/json' } }),
};

export const cvEducationApi = {
  getByCv: (cvId) => api.get(`/cv-educations?cvId=${cvId}`),
  replaceForCv: (cvId, candidateEducationIds) =>
    api.put('/cv-educations', { cvId, candidateEducationIds }, { headers: { 'Content-Type': 'application/json' } }),
};
