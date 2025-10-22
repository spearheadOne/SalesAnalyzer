import {createPeriodStore, PeriodStoreContext} from "../../store/periodStore.ts";
import {beforeEach, describe, expect, it} from "vitest";
import {fireEvent, render, screen, within} from "@testing-library/react";
import Period from "../Period.tsx";
import userEvent from "@testing-library/user-event";

let testStore: ReturnType<typeof createPeriodStore>;

beforeEach(() => {
    testStore = createPeriodStore();
})


describe('Period', () => {
    it('should render period component', () => {
        testStore.setState({period: '1m'})
        render(
            <PeriodStoreContext.Provider value={testStore}>
                <Period/>
            </PeriodStoreContext.Provider>
        )

        const input = screen.getByRole('spinbutton')
        expect((input as HTMLInputElement).value).to.be.equal('1')

        const button = screen.getByRole('button')
        expect(button.textContent?.includes('m')).to.be.true
    })

    it('should render all options from store', () => {
        render(
            <PeriodStoreContext.Provider value={testStore}>
                <Period/>
            </PeriodStoreContext.Provider>
        )

        const menu = screen.getByRole('menu')
        const items = within(menu).getAllByRole('menuitem')
        expect(items.length).to.be.equal(testStore.getState().units.length)
    });

    it('should update store when set period and unit', async () => {
        const user = userEvent.setup();
        testStore.setState({period: '1m'})
        render(
            <PeriodStoreContext.Provider value={testStore}>
                <Period/>
            </PeriodStoreContext.Provider>
        )

        const input = screen.getByRole('spinbutton') as HTMLInputElement

        fireEvent.change(input, {target: {value: '10'}})

        const menu = screen.getByRole('menu')
        const optDay = within(menu).getByRole('menuitem', {name: 'd'})
        await user.click(optDay)
        expect(testStore.getState().period).to.be.equal('10d')
    });


})
