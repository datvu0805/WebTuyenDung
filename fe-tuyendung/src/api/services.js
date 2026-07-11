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
  create: (data) => api.post('/jobs', new URLSearchParams(data)),
  update: (data) => api.put('/jobs', new URLSearchParams(data)),
  delete: (id) => api.delete(`/jobs?id=${id}`),
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
  purchase: (userID, packageID) =>
    api.post('/api/payment/purchase', { userID, packageID }, {
      headers: { 'Content-Type': 'application/json' },
    }),
};

export const certificateApi = {
  getAll: () => api.get('/certificate'),
  getById: (id) => api.get(`/certificate/${id}`),
  create: (data) => api.post('/certificate', new URLSearchParams(data)),
  update: (data) => api.put('/certificate', new URLSearchParams(data)),
  delete: (id) => api.delete(`/certificate?id=${id}`),
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
};
