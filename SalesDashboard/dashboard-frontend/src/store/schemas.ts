import {z} from 'zod'

//todo: add currency fields
export const CategoryRevenueSchema = z.object({
    category: z.string(),
    revenue: z.number()
})

export const ProductsRevenueSchema = z.object({
    productId: z.string(),
    productName: z.string(),
    revenue: z.number(),
    orders: z.number(),
    units: z.number()
})

export const TimeSeriesPointSchema = z.object({
    eventTime: z.coerce.date(),
    productId: z.string(),
    productName: z.string(),
    revenue: z.number()
})

export const AggRowSchema = z.object({
        eventTime: z.coerce.date(),
        productId: z.string(),
        productName: z.string(),
        category: z.string(),
        orders: z.number(),
        units: z.number(),
        revenue: z.number()
    })

export const CategoryRevenueListSchema = z.array(CategoryRevenueSchema)
export const ProductsRevenueListSchema = z.array(ProductsRevenueSchema)
export const TimeSeriesPointListSchema = z.array(TimeSeriesPointSchema)

export type CategoryRevenue = z.infer<typeof CategoryRevenueSchema>
export type ProductsRevenue = z.infer<typeof ProductsRevenueSchema>
export type TimeSeriesPoint = z.infer<typeof TimeSeriesPointSchema>


export type ResponseMap = {
    categoryResponse: CategoryRevenue[];
    productsResponse: ProductsRevenue[];
    timeSeriesResponse: TimeSeriesPoint[];
}

export const DataKeys = {
    Category: 'categoryResponse',
    Products: 'productsResponse',
    TimeSeries: 'timeSeriesResponse',
} as const

export type DataType = typeof DataKeys[keyof typeof DataKeys]

export const schemaMap: { [K in DataType]: z.ZodType<ResponseMap[K]>} = {
    categoryResponse: CategoryRevenueListSchema,
    productsResponse: ProductsRevenueListSchema,
    timeSeriesResponse: TimeSeriesPointListSchema,
}

