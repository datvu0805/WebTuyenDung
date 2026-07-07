import axios from 'axios';

const api = axios.create({
  baseURL: import.meta.env.VITE_API_BASE_URL || 'http://localhost:8080/AppTuyenDung',
  withCredentials: true, // gửi session cookie
  headers: { 'Content-Type': 'application/x-www-form-urlencoded' },
});

// Tự động redirect về login khi hết phiên
api.interceptors.response.use(
  (res) => res,
  (err) => {
    if (err.response?.status === 401) {
      window.location.href = '/login';
    }
    return Promise.reject(err);
  }
);

export default api;
