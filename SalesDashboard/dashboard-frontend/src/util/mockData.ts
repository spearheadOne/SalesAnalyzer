import type {CategoryRevenue, ProductsRevenue, TimeSeriesPoint} from "../store/schemas.ts";


// Mock data for testing visuals without calling the backend
export const MOCK_CATEGORY_DATA: CategoryRevenue[] = [
    { category: 'Electronics', revenue: 15234.75 },
    { category: 'Books', revenue: 8230.10 },
    { category: 'Home & Kitchen', revenue: 11500.50 },
    { category: 'Clothing', revenue: 9340.00 },
    { category: 'Sports', revenue: 4860.30 },
]

export const MOCK_PRODUCT_DATA: ProductsRevenue[] = [
    {
        productId: 'P-1001',
        productName: 'Wireless Headphones',
        revenue: 15230.75,
        orders: 210,
        units: 2100,
    },
    {
        productId: 'P-1002',
        productName: 'Gaming Mouse',
        revenue: 8920.10,
        orders: 180,
        units: 120,
    },
    {
        productId: 'P-1003',
        productName: 'Mechanical Keyboard',
        revenue: 7480.50,
        orders: 130,
        units: 140,
    },
    {
        productId: 'P-1004',
        productName: '4K Monitor',
        revenue: 21600.00,
        orders: 95,
        units: 945,
    },
    {
        productId: 'P-1005',
        productName: 'Smartwatch',
        revenue: 11450.20,
        orders: 260,
        units: 2000,
    },
]

export const MOCK_TIME_SERIES: TimeSeriesPoint[] = [
    { eventTime: isoMinutesAgo(200), productId: 'P-1001', productName: '4K Monitor 27"', revenue: 420.00 },
    { eventTime: isoMinutesAgo(200), productId: 'P-2001', productName: 'Noise-cancel Headphones', revenue: 210.00 },
    { eventTime: isoMinutesAgo(15), productId: 'P-1001', productName: '4K Monitor 27"', revenue: 210.00 },
    { eventTime: isoMinutesAgo(150), productId: 'P-4001', productName: 'Mechanical KB',  revenue:  95.00 },
    { eventTime: isoMinutesAgo(10), productId: 'P-5001', productName: 'USB-C Dock',     revenue: 120.00 },
    { eventTime: isoMinutesAgo(10), productId: 'P-2001', productName: 'Noise-cancel Headphones', revenue: 150.00 },
    { eventTime: isoMinutesAgo(500),  productId: 'P-3001', productName: 'Ergo Chair',     revenue: 380.00 },
    { eventTime: isoMinutesAgo(50),  productId: 'P-1001', productName: '4K Monitor 27"', revenue: 210.00 },
    { eventTime: isoMinutesAgo(0),  productId: 'P-4001', productName: 'Mechanical KB',  revenue: 110.00 },
    { eventTime: isoMinutesAgo(0),  productId: 'P-2001', productName: 'Noise-cancel Headphones', revenue: 160.00 },
];

function isoMinutesAgo(mins: number): Date {
    const d = new Date();
    d.setMinutes(d.getMinutes() - mins);
    return d
}