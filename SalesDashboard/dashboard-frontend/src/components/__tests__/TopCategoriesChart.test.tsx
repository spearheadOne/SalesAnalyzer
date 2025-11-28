import {useHistoricDataStore} from "../../store/historicDataStore.ts";
import {beforeAll, beforeEach, describe, expect, it} from "vitest";
import {render, screen} from "@testing-library/react";
import TopCategoriesChart from "../TopCategoriesChart.tsx";

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

describe('TopCategoriesChart', () => {
    it('renders with data', () => {
        useHistoricDataStore.setState({
            categoryResponse: {
                defaultCurrency: "EUR",
                items: [
                    {category: 'Electronics', revenue: 15000},
                    {category: 'Books', revenue: 8000}
                ],
            }
        })

        render(<TopCategoriesChart/>)

        expect(document.querySelector('svg')).not.toBeNull
    })

    it('renders with no data', () => {
        useHistoricDataStore.setState({categoryResponse: null})

        render(<TopCategoriesChart/>)
        expect(screen.getByText(/no data available/i)).not.toBeNull
    })
})