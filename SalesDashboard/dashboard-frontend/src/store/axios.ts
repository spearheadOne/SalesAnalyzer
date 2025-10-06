import axios from 'axios';

const api = axios.create({baseURL: 'http://localhost:9024/dashboard/'})

export default api;