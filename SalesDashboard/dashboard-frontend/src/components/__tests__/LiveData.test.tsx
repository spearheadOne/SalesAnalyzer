import {useLiveDataStore} from "../../store/liveDataStore.ts";
import {beforeAll, beforeEach, describe, expect, it} from "vitest";
import {render, screen} from "@testing-library/react";
import LiveData from "../LiveData.tsx";


const testState = useLiveDataStore.getState()

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
    useLiveDataStore.setState(testState, true)
})

describe('LiveData', () => {
    it('render with data', () => {
        useLiveDataStore.setState({
            liveData: [
                {
                    eventTime: new Date("2025-01-01T12:00:00Z"),
                    productId: "P-1001",
                    productName: "Wireless Mouse",
                    category: "Electronics",
                    orders: 3,
                    units: 5,
                    revenue: 149.99,
                    currency: "EUR",
                }
            ]
        })

        render(<LiveData/>)
        expect(document.querySelector('svg')).not.toBeNull
    })

    it('renders without data', () => {
        useLiveDataStore.setState({liveData: null})

        render(<LiveData/>)
        expect(screen.getByText(/no data available/i)).not.toBeNull
    })
})
