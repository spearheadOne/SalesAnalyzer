import type {CategoryRevenue, ProductsRevenue} from "../store/schemas.ts";


// Mock data for testing visuals without calling the backend


export const mockCategoryData: CategoryRevenue[] = [
    { category: 'Electronics', revenue: 15234.75 },
    { category: 'Books', revenue: 8230.10 },
    { category: 'Home & Kitchen', revenue: 11500.50 },
    { category: 'Clothing', revenue: 9340.00 },
    { category: 'Sports', revenue: 4860.30 },
]

export const mockProductsData: ProductsRevenue[] = [
    {
        productId: 'P001',
        productName: 'Wireless Headphones',
        revenue: 15230.75,
        orders: 210,
        units: 210,
    },
    {
        productId: 'P002',
        productName: 'Gaming Mouse',
        revenue: 8920.10,
        orders: 180,
        units: 180,
    },
    {
        productId: 'P003',
        productName: 'Mechanical Keyboard',
        revenue: 7480.50,
        orders: 130,
        units: 130,
    },
    {
        productId: 'P004',
        productName: '4K Monitor',
        revenue: 21600.00,
        orders: 95,
        units: 95,
    },
    {
        productId: 'P005',
        productName: 'Smartwatch',
        revenue: 11450.20,
        orders: 260,
        units: 260,
    },
]