import {useHistoricDataStore} from "../../store/historicDataStore.ts";
import {makePeriodStore} from "../../store/periodState.ts";
import {describe, expect, it, beforeEach, beforeAll} from "vitest";
import {render, screen} from "@testing-library/react";
import TopCategoriesChart from "../TopCategoriesChart.tsx";

const initHistoric = useHistoricDataStore.getState()
const initPeriod = makePeriodStore.getState()


beforeAll(() => {
    global.ResizeObserver = class {
        observe() {}
        unobserve() {}
        disconnect() {}
    }
})

beforeEach(() => {
    useHistoricDataStore.setState(initHistoric, true)
    makePeriodStore.setState({ ...initPeriod, period: '1m'}, true)
})

describe('TopCategoriesChart', () => {
    it('renders with data', () => {
        useHistoricDataStore.setState({
            categoryResponse: [
                { category: 'Electronics', revenue: 15000 },
                { category: 'Books',       revenue: 8000  },
            ],
        })

        render(<TopCategoriesChart />)

        expect(document.querySelector('svg')).not.toBeNull
    })

    it('renders with no data', () => {
        useHistoricDataStore.setState({ categoryResponse: null })
        render(<TopCategoriesChart />)
        expect(screen.getByText(/no data available/i)).not.toBeNull
    })
})