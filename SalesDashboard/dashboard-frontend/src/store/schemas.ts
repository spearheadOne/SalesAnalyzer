import {z} from 'zod'
import {parseNumericString} from "../util/util.ts";

export const CategoryRevenueItemSchema = z.object({
    category: z.string(),
    revenue: z.preprocess(parseNumericString, z.number()),
    currency: z.string().optional()
})

export const ProductsRevenueItemSchema = z.object({
    productId: z.string(),
    productName: z.string(),
    revenue: z.preprocess(parseNumericString, z.number()),
    currency: z.string().optional(),
    orders: z.number(),
    units: z.number()
})

export const TimeSeriesPointDtoSchema = z.object({
    eventTime: z.coerce.date(),
    productId: z.string(),
    productName: z.string(),
    revenue: z.preprocess(parseNumericString, z.number()),
    currency: z.string().optional()
})

export const CategoryRevenueResponseSchema = z.object({
    defaultCurrency: z.string().optional(),
    items: z.array(CategoryRevenueItemSchema)
});

export const ProductsRevenueResponseSchema = z.object({
    defaultCurrency: z.string().optional(),
    items: z.array(ProductsRevenueItemSchema)
});

export const TimeSeriesResponseSchema = z.object({
    defaultCurrency: z.string().optional(),
    points: z.array(TimeSeriesPointDtoSchema)
});

export const AggRowSchema = z.object({
    eventTime: z.coerce.date(),
    productId: z.string(),
    productName: z.string(),
    category: z.string(),
    orders: z.number(),
    units: z.number(),
    revenue: z.preprocess(parseNumericString, z.number()),
    currency: z.string().optional(),
    origPrice: z.object({
        price: z.preprocess(parseNumericString, z.number()),
        currency: z.string()
    })
        .optional()
})


export type CategoryRevenueResponse = z.infer<typeof CategoryRevenueResponseSchema>
export type ProductsRevenueResponse = z.infer<typeof ProductsRevenueResponseSchema>
export type TimeSeriesResponse = z.infer<typeof TimeSeriesResponseSchema>
export type AggRow = z.infer<typeof AggRowSchema>

export type CategoryRevenue = z.infer<typeof CategoryRevenueItemSchema>
export type ProductsRevenue = z.infer<typeof ProductsRevenueItemSchema>
export type TimeSeriesPoint = z.infer<typeof TimeSeriesPointDtoSchema>


export type ResponseMap = {
    categoryResponse: CategoryRevenueResponse;
    productsResponse: ProductsRevenueResponse;
    timeSeriesResponse: TimeSeriesResponse;
};

export const DataKeys = {
    Category: 'categoryResponse',
    Products: 'productsResponse',
    TimeSeries: 'timeSeriesResponse',
} as const

export type DataType = typeof DataKeys[keyof typeof DataKeys]

export const schemaMap: { [K in DataType]: z.ZodType<ResponseMap[K]> } = {
    categoryResponse: CategoryRevenueResponseSchema,
    productsResponse: ProductsRevenueResponseSchema,
    timeSeriesResponse: TimeSeriesResponseSchema,
};