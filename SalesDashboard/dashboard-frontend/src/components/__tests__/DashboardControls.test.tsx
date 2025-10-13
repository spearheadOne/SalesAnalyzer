import {describe, it, vi, expect} from "vitest";
import {render, screen} from "@testing-library/react";
import DashboardControls from "../DashboardControls.tsx";


describe('DashboardControls', () => {
    it('should render dashboard controls', () => {
        const fetch = vi.fn()
        render(<DashboardControls fetchData={fetch} limitEnabled/>)

        //period
        expect(screen.getByRole('spinbutton')).not.toBeNull()

        //limit
        expect(screen.getByRole('button', { name: /10/ })).not.toBeNull()

        //fetch button
        expect(screen.getByRole('button', { name: /Get data/i })).not.toBeNull()
    })

    it('should render dashboard controls without limit', () => {
        const fetch = vi.fn()
        render(<DashboardControls fetchData={fetch} limitEnabled={false}/>)

        expect(screen.getByRole('spinbutton')).not.toBeNull()
        expect(screen.queryByText(/Records:/i)).toBeNull
    })
})