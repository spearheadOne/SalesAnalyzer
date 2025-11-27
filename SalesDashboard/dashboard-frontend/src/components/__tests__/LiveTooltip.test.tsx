import {describe, it, expect} from "vitest";
import {render, screen} from "@testing-library/react";
import {LiveTooltip} from "../LiveTooltip.tsx";
import type {AggRow} from "../../store/schemas.ts";
import type {TooltipProps} from "recharts";


describe('LiveTooltip', ()=>{
    it('should render a tooltip',()=>{
        const mockRow: AggRow = {
            eventTime: new Date("2025-01-01T12:00:00Z"),
            productId: "P-1001",
            productName: "Wireless Mouse",
            category: "Electronics",
            orders: 3,
            units: 5,
            revenue: 149.99,
            currency: "EUR",
            origPrice: {
                price: 39.99,
                currency: "EUR",
            },
        };

        const payload: TooltipProps<number, string>["payload"] = [
            {
                value: mockRow.revenue,
                name: "revenue",
                color: "#8884d8",
                payload: mockRow,
            },
        ];


        render(
            <LiveTooltip
                active={true}
                payload={payload}
                label={mockRow.eventTime.toISOString()}
            />
        )

        expect(screen.getByText(/Category: Electronics/)).not.toBeNull
        expect(screen.getByText(/Orders: 3/)).not.toBeNull
        expect(screen.getByText(/Units: 5/)).not.toBeNull
        expect(screen.getByText(/Revenue: 149\.99 EUR/)).not.toBeNull
        expect(screen.getByText(/Orig\. price: 39\.99 EUR/)).not.toBeNull
    })
})