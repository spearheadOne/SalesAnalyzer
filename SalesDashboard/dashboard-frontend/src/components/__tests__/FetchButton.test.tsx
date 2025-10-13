import {describe, expect, it, vi} from "vitest";
import FetchButton from "../FetchButton.tsx";
import {render, screen} from "@testing-library/react";
import userEvent from "@testing-library/user-event";

describe('FetchButton', () => {
    it('should render fetch button', () => {
        render(<FetchButton fetchData={() => {
        }}/>)

        const button = screen.getByRole('button', {name: /Get data/i})
        expect(button).not.to.be.null
        expect(button.getAttribute('type')).to.be.equal('submit')
    })

    it('should fetch data on click', async () => {
        const fetchData = vi.fn()
        render(<FetchButton fetchData={fetchData}/>)

        const user = userEvent.setup();
        await user.click(screen.getByRole('button', {name: /Get data/i}))

        expect(fetchData).toHaveBeenCalledTimes(1)
    })

})