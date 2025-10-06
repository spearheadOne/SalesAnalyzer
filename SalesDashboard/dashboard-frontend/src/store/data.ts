export interface AggRow {
    eventTime?: string;
    productId?: string;
    productName?: string;
    category?: string;
    orders?: number;
    units?: number;
    revenue?: number;
}

export interface CategoryRevenue {
    category?: string;
    revenue?: number;
}

export interface ProductsRevenue {
    productId?: string;
    productName?: string;
    revenue?: number;
    orders?: number;
    units?: number;
}

export interface TimeSeriesPoint {
    eventTime?: string;
    productId?: string;
    productName?: string;
    revenue?: number;
}