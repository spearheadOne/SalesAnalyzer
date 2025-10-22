import {createLimitStore, LimitStoreContext} from "../../store/limitStore";
import Limit from "../Limit";
import {render, screen, within} from "@testing-library/react";
import {beforeEach, describe, expect, it} from 'vitest'
import userEvent from "@testing-library/user-event"


let testStore: ReturnType<typeof createLimitStore>;

beforeEach(() => {
    testStore = createLimitStore();
})

describe('Limit', () => {
    it('should render limit component', () => {
        testStore.setState({limit: 10});
        render(
            <LimitStoreContext.Provider value={testStore}>
                <Limit/>
            </LimitStoreContext.Provider>
        );

        const button = screen.getByRole('button')
        expect(button.textContent?.includes('10')).to.be.true
    })

    it('should render all options from store', () => {
        render(
            <LimitStoreContext.Provider value={testStore}>
                <Limit/>
            </LimitStoreContext.Provider>
        );

        const menu = screen.getByRole('menu')
        const items = within(menu).getAllByRole('menuitem')
        expect(items.length).to.be.equal(testStore.getState().limits.length)
    })

    it('should change limit on click', async () => {
        const user = userEvent.setup();
        render(
            <LimitStoreContext.Provider value={testStore}>
                <Limit/>
            </LimitStoreContext.Provider>
        );

        const menu = screen.getByRole('menu')
        const opt50 = within(menu).getByRole('menuitem', {name: '50'})
        await user.click(opt50)
        expect(testStore.getState().limit).to.be.equal(50)

        const button = screen.getByRole('button')
        expect(button.textContent?.includes('50')).to.be.true
    })

});