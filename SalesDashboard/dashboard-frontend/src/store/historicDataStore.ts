import {create} from "zustand/react";
import api, {buildUrl} from './client.ts';
import {
    type CategoryRevenueResponse,
    DataKeys,
    type DataType,
    type ProductsRevenueResponse,
    type ResponseMap,
    schemaMap,
    type TimeSeriesResponse
} from "./schemas.ts";
import {z} from "zod";

interface HistoricDataStore {
    categoryResponse: CategoryRevenueResponse | null;
    productsResponse: ProductsRevenueResponse | null;
    timeSeriesResponse: TimeSeriesResponse | null;

    fetchCategoryRevenue: (period: string, limit: number) => Promise<void>;
    fetchProductsRevenue: (period: string, limit: number) => Promise<void>;
    fetchTimeSeries: (period: string) => Promise<void>;
}

export const useHistoricDataStore = create<HistoricDataStore>((set) => {

    const fetchData = async <T extends DataType>(dataType: T, uri: string) => {
        set({[dataType]: null, error: undefined} as Partial<HistoricDataStore>);
        const res = await api.get(uri);
        const parsed = (schemaMap[dataType] as z.ZodTypeAny).parse(res.data) as ResponseMap[T];
        set({[dataType]: parsed} as Partial<HistoricDataStore>)
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