import {beforeEach, describe, expect, it, vi} from "vitest";
import {render, screen} from "@testing-library/react";
import DashboardControls from "../DashboardControls.tsx";
import {createPeriodStore, PeriodStoreContext} from "../../store/periodStore.ts";
import {createLimitStore, LimitStoreContext} from "../../store/limitStore.ts";


let periodStore: ReturnType<typeof createPeriodStore>;
let limitStore: ReturnType<typeof createLimitStore>;

beforeEach(() => {
    periodStore = createPeriodStore();
    limitStore = createLimitStore();
})

describe('DashboardControls', () => {
    it('should render dashboard controls', () => {
        const fetch = vi.fn()
        render(
            <PeriodStoreContext.Provider value={periodStore}>
                <LimitStoreContext.Provider value={limitStore}>
                    <DashboardControls fetchData={fetch} limitEnabled/>
                </LimitStoreContext.Provider>
            </PeriodStoreContext.Provider>
        )

        //period
        expect(screen.getByRole('spinbutton')).not.toBeNull()

        //limit
        expect(screen.getByRole('button', {name: /10/})).not.toBeNull()

        //fetch button
        expect(screen.getByRole('button', {name: /Get data/i})).not.toBeNull()
    })

    it('should render dashboard controls without limit', () => {
        const fetch = vi.fn()
        render(
            <PeriodStoreContext.Provider value={periodStore}>
                <LimitStoreContext.Provider value={limitStore}>
                    <DashboardControls fetchData={fetch} limitEnabled={false}/>
                </LimitStoreContext.Provider>
            </PeriodStoreContext.Provider>
        )

        expect(screen.getByRole('spinbutton')).not.toBeNull()
        expect(screen.queryByText(/Records:/i)).toBeNull
    })
})