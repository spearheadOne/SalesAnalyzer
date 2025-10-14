import {describe, expect, it, vi} from "vitest";
import {render, screen} from "@testing-library/react";
import {DataCard} from "../DataCard.tsx";
import {usePeriodStore} from "../../store/periodStore.ts";

describe('DataCard', () => {
    it('should render data card with zero children', () => {
        const fetchData = vi.fn()
        usePeriodStore.setState({period: '1m'})

        render(<DataCard
            title={"test"}
            dataCount={0}
            children={
                <div>test</div>
            }
            limitEnabled={false}
            fetchData={fetchData}/>)

        expect(screen.getByText(/no data available/i)).not.toBeNull
        expect(screen.getByText(/test 1 minute/i)).not.toBeNull
        expect(screen.getByText(/No data available/i)).not.toBeNull
    })

    it('should render data card with one child', () => {
        const fetchData = vi.fn()
        usePeriodStore.setState({period: '1m'})

        render(<DataCard
            title={"test"}
            dataCount={1}
            children={
                <div>test child</div>
            }
            limitEnabled={false}
            fetchData={fetchData}/>)

        expect(screen.getByText(/test child/i)).not.toBeNull
    })
})