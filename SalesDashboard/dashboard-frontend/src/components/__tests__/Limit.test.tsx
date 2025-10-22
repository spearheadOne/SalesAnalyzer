import {useLimitStore} from "../../store/limitStore";
import Limit from "../Limit";
import {render, screen, within} from "@testing-library/react";
import {beforeEach, describe, expect, it} from 'vitest'
import userEvent from "@testing-library/user-event"

const initState = useLimitStore.getState();
beforeEach(() => {
    useLimitStore.setState(initState, true);
})


describe('Limit', () => {
    it('should render limit component', () => {
        useLimitStore.setState({limit: 10});
        render(<Limit/>);

        const button = screen.getByRole('button')
        expect(button.textContent?.includes('10')).to.be.true
    })

    it('should render all options from store', () => {
        render(<Limit/>);

        const menu = screen.getByRole('menu')
        const items = within(menu).getAllByRole('menuitem')
        expect(items.length).to.be.equal(useLimitStore.getState().limits.length)
    })

    it('should change limit on click', async () => {
        const user = userEvent.setup();
        render(<Limit/>)

        const menu = screen.getByRole('menu')
        const opt50 = within(menu).getByRole('menuitem', {name: '50'})
        await user.click(opt50)
        expect(useLimitStore.getState().limit).to.be.equal(50)

        const button = screen.getByRole('button')
        expect(button.textContent?.includes('50')).to.be.true
    })

});