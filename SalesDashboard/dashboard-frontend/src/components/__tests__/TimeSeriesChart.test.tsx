import {useHistoricDataStore} from "../../store/historicDataStore.ts";
import {beforeAll, beforeEach, describe, expect, it} from "vitest";
import {render, screen} from "@testing-library/react";
import TimeSeriesChart from "../TimeSeriesChart.tsx";

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

describe('TimeSeriesChart', () => {
    it('renders with data', () => {
        useHistoricDataStore.setState({
            timeSeriesResponse: [
                {
                    eventTime: new Date(),
                    productId: '',
                    productName: 'Mechanical KB',
                    revenue: 110.00
                }
            ],
        })

        render(<TimeSeriesChart/>)
        expect(document.querySelector('svg')).not.toBeNull
    })

    it('renders without data', () => {
        useHistoricDataStore.setState({timeSeriesResponse: null})

        render(<TimeSeriesChart/>)
        expect(screen.getByText(/no data available/i)).not.toBeNull
    })
})