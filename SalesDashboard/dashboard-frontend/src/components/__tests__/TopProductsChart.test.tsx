import {useHistoricDataStore} from "../../store/historicDataStore.ts";
import {beforeAll, beforeEach, describe, expect, it} from "vitest";
import {render, screen} from "@testing-library/react";
import TopProductsChart from "../TopProductsChart.tsx";


const testHistoricState = useHistoricDataStore.getState()

beforeAll(() => {
    global.ResizeObserver = class {
        observe() {
        }

        unobserve() {
        }

        disconnect() {
        }
    }
})

beforeEach(() => {
    useHistoricDataStore.setState(testHistoricState, true)
})

describe('TopProductsChart', () => {
    it('renders with data', () => {
        useHistoricDataStore.setState({
            productsResponse: {
                defaultCurrency: "EUR",
                items: [
                    {productId: '1', productName: 'Product 1', revenue: 100, orders: 10, units: 10},
                    {productId: '2', productName: 'Product 2', revenue: 200, orders: 20, units: 20},
                ],
            }
        })

        render(<TopProductsChart/>)

        expect(document.querySelector('svg')).not.toBeNull
    })

    it('renders with no data', () => {
        useHistoricDataStore.setState({ productsResponse: null })

        render(<TopProductsChart/>)
        expect(screen.getByText(/no data available/i)).not.toBeNull
    })
})