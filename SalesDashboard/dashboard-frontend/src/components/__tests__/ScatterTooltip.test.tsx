import {describe, expect, it} from "vitest";
import ScatterTooltip from "../ScatterTooltip.tsx";
import {render, screen} from "@testing-library/react";


describe('ScatterTooltip', () => {
    it('should render tooltip with point', () => {

        const point = {
            eventTime: new Date(),
            productId: '',
            productName: 'Mechanical KB',
            revenue: 110.00
        }

        render(<ScatterTooltip point={point}/>)

        expect(screen.getByText(/Product:/i)).not.toBeNull
        expect(screen.getByText(/Mechanical KB/i)).not.toBeNull
        expect(screen.getByText(/ID:/i)).not.toBeNull
    })
})