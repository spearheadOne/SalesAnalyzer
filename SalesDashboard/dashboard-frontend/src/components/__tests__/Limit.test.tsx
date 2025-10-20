import {makeLimitStore} from "../../store/limitState.ts";
import Limit from "../Limit";
import {render, screen, within} from "@testing-library/react";
import { describe, it, expect, beforeEach } from 'vitest'
import userEvent from "@testing-library/user-event"

const initState = makeLimitStore.getState();
beforeEach(() => {
    makeLimitStore.setState(initState, true);
})


describe('Limit', () => {
    it('should render limit component', () => {
        makeLimitStore.setState({limit: 10});
        render(<Limit/>);

        const button = screen.getByRole('button')
        expect(button.textContent?.includes('10')).to.be.true
    })

    it('should render all options from store',() => {
        render(<Limit/>);

        const menu = screen.getByRole('menu')
        const items = within(menu).getAllByRole('menuitem')
        expect(items.length).to.be.equal(makeLimitStore.getState().limits.length)
    })

    it('should change limit on click', async () => {
        const user = userEvent.setup();
        render(<Limit/>)

        const menu = screen.getByRole('menu')
        const opt50 = within(menu).getByRole('menuitem', {name: '50'})
        await user.click(opt50)
        expect(makeLimitStore.getState().limit).to.be.equal(50)

        const button = screen.getByRole('button')
        expect(button.textContent?.includes('50')).to.be.true
    })

});