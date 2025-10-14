
import {create} from "zustand/react";
import api from './axios.ts';
import {
    type CategoryRevenue, DataKeys, type DataType,
    type ProductsRevenue,
    type ResponseMap,
    schemaMap,
    type TimeSeriesPoint
} from "./schemas.ts";

interface HistoricDataStore {

    categoryResponse: CategoryRevenue[] | null;
    productsResponse: ProductsRevenue[] | null;
    timeSeriesResponse: TimeSeriesPoint[] | null;

    error?: string;

    fetchCategoryRevenue: (period: string, limit: number) => Promise<void>;
    fetchProductsRevenue: (period: string, limit: number) => Promise<void>;
    fetchTimeSeries: (period: string) => Promise<void>;

}

export const useHistoricDataStore = create<HistoricDataStore>((set) => {

    const fetchData=  async <T extends DataType>(dataType: T, uri: string) => {
        set({[dataType]: null, error: undefined} as Partial<HistoricDataStore>);
        try {
            const res = await api.get(uri);
            const parsed = schemaMap[dataType].parse(res.data) as ResponseMap[T];
            set({[dataType]: parsed} as Partial<HistoricDataStore>)
        } catch (err: any) {
            const message = err?.name === 'ZodError' ? 'Invalid server data' : (err?.message || 'Request failed')
            set({ error: message })
        }
    }

    const buildUrl = (path: string, period: string, limit?: number) => {
        if (limit != null) {
            return `${path}/${encodeURIComponent(period)}?limit=${limit}`
        } else {
            return `${path}/${encodeURIComponent(period)}`
        }
    }

    return {
        categoryResponse: null,
        productsResponse: null,
        timeSeriesResponse: null,
        error: undefined,

        fetchCategoryRevenue: async (period: string, limit: number) =>
            fetchData(DataKeys.Category, buildUrl('categories', period, limit)),

        fetchProductsRevenue: async (period: string, limit: number) =>
            fetchData(DataKeys.Products, buildUrl('products', period, limit)),

        fetchTimeSeries: async (period: string) =>
            fetchData(DataKeys.TimeSeries, buildUrl('time-series', period))
    }

})