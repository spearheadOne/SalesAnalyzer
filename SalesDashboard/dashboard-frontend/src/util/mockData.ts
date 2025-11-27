// Mock data for testing visuals without calling the backend
import type {AggRow} from "../store/schemas.ts";

export const MOCK_CATEGORY_DATA = {
    defaultCurrency: 'EUR',
    items: [
        { category: 'Electronics', revenue: '15234.75', currency: 'EUR' },
        { category: 'Books', revenue: '8230.10', currency: 'EUR' },
        { category: 'Home & Kitchen', revenue: '11500.50', currency: 'EUR' },
        { category: 'Clothing', revenue: '9340.00', currency: 'EUR' },
        { category: 'Sports', revenue: '4860.30', currency: 'EUR' }
    ]
};

export const MOCK_PRODUCT_DATA = {
    defaultCurrency: 'EUR',
    items: [
        { productId: 'P-1001', productName: 'Wireless Headphones', revenue: '15230.75', currency: 'EUR', orders: 210, units: 2100 },
        { productId: 'P-1002', productName: 'Gaming Mouse', revenue: '8920.10', currency: 'EUR', orders: 180, units: 120 },
        { productId: 'P-1003', productName: 'Mechanical Keyboard', revenue: '7480.50', currency: 'EUR', orders: 130, units: 140 },
        { productId: 'P-1004', productName: '4K Monitor', revenue: '21600.00', currency: 'EUR', orders: 95, units: 945 },
        { productId: 'P-1005', productName: 'Smartwatch', revenue: '11450.20', currency: 'EUR', orders: 260, units: 2000 }
    ]
};

export const MOCK_TIME_SERIES = {
    defaultCurrency: 'EUR',
    points: [
        { eventTime: isoMinutesAgo(200), productId: 'P-1001', productName: '4K Monitor 27"', revenue: '420.00', currency: 'EUR' },
        { eventTime: isoMinutesAgo(200), productId: 'P-2001', productName: 'Noise-cancel Headphones', revenue: '210.00', currency: 'EUR' },
        { eventTime: isoMinutesAgo(15), productId: 'P-1001', productName: '4K Monitor 27"', revenue: '210.00', currency: 'EUR' },
        { eventTime: isoMinutesAgo(150), productId: 'P-4001', productName: 'Mechanical KB',  revenue:  95.00 },
        { eventTime: isoMinutesAgo(10), productId: 'P-5001', productName: 'USB-C Dock',     revenue: 120.00 },
        { eventTime: isoMinutesAgo(10), productId: 'P-2001', productName: 'Noise-cancel Headphones', revenue: 150.00 },
        { eventTime: isoMinutesAgo(500),  productId: 'P-3001', productName: 'Ergo Chair',     revenue: 380.00 },
        { eventTime: isoMinutesAgo(50),  productId: 'P-1001', productName: '4K Monitor 27"', revenue: 210.00 },
        { eventTime: isoMinutesAgo(0),  productId: 'P-4001', productName: 'Mechanical KB',  revenue: 110.00 },
        { eventTime: isoMinutesAgo(0),  productId: 'P-2001', productName: 'Noise-cancel Headphones', revenue: 160.00 }
    ]
};


export const MOCK_AGG_ROWS: AggRow[] = [
    {
        eventTime: new Date("2025-01-01T12:00:00.000Z"),
        productId: "P-1001",
        productName: "Wireless Mouse",
        category: "Electronics",
        orders: 51,
        units: 5232,
        revenue: 149.95,
        currency: "EUR",
        origPrice: { price: 39.99, currency: "EUR" }
    },
    {
        eventTime: new Date("2025-01-01T12:00:01.000Z"),
        productId: "P-2042",
        productName: "Coffee Beans Premium 1kg",
        category: "Groceries",
        orders: 267,
        units: 221321,
        revenue: 44.00,
        currency: "EUR",
        origPrice: { price: 19.99, currency: "EUR" }
    },
    {
        eventTime: new Date("2025-01-01T12:00:02.000Z"),
        productId: "P-9003",
        productName: "Gaming Keyboard RGB",
        category: "Electronics",
        orders: 300,
        units: 355,
        revenue: 199.50,
        currency: "EUR",
        origPrice: { price: 69.99, currency: "EUR" }
    }
];

function isoMinutesAgo(mins: number): Date {
    const d = new Date();
    d.setMinutes(d.getMinutes() - mins);
    return d
}