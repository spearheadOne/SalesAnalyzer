import axios from 'axios';


const API_BASE_URL = 'http://localhost:9024/dashboard/'

const api = axios.create({baseURL: API_BASE_URL})

export function buildUrl(path: string, period: string, limit?: number){
    if (limit != null) {
        return `${path}/${encodeURIComponent(period)}?limit=${limit}`
    } else {
        return `${path}/${encodeURIComponent(period)}`
    }
}

export const STREAM_URL = API_BASE_URL + '/stream'


export default api;