import type {CategoryRevenue, ProductsRevenue, TimeSeriesPoint} from "./data.ts";
import {create} from "zustand/react";
import api from './axios.ts';

interface HistoricDataStore {

    categoryResponse: CategoryRevenue[] | null;
    productsResponse: ProductsRevenue[] | null;
    timeSeriesResponse: TimeSeriesPoint[] | null;

    error?: string;

    fetchCategoryRevenue: (period: string, limit: number) => Promise<void>;
    fetchProductsRevenue: (period: string, limit: number) => Promise<void>;
    fetchTimeSeries: (period: string) => Promise<void>;

}

type DataType = keyof Pick<
    HistoricDataStore,
    'categoryResponse' | 'productsResponse' | 'timeSeriesResponse'
>

export const useHistoricDataStore = create<HistoricDataStore>((set) => {

    const fetchData = async <T>(dataType: DataType, uri: string) => {
        set({[dataType]: null, error: undefined} as Partial<HistoricDataStore>);

        try {
            const res = await api.get<T>(uri);
            set({[dataType]: res.data} as Partial<HistoricDataStore>)
        } catch (err: any) {
            set({error: err?.message || 'Request failed'});
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
            fetchData('categoryResponse', buildUrl('categories', period, limit)),

        fetchProductsRevenue: async (period: string, limit: number) =>
            fetchData('productsResponse', buildUrl('products', period, limit)),

        fetchTimeSeries: async (period: string) =>
            fetchData('timeSeriesResponse', buildUrl('timeseries', period))
    }

})