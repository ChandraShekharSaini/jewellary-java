import axios from 'axios';

const api = axios.create({
  baseURL: 'http://backend-load-471836027.us-east-1.elb.amazonaws.com/api/v1',
});

api.interceptors.request.use((config) => {
  const token = localStorage.getItem('token');
  if (token) {
    config.headers.Authorization = `Bearer ${token}`;
  }
  return config;
});

export default api;
