import {describe, expect, it, vi} from "vitest";
import {fireEvent, render, screen, waitFor, within} from "@testing-library/react";
import {DataCard} from "../DataCard.tsx";
import userEvent from "@testing-library/user-event";

describe('DataCard', () => {
    it('should render a data card with zero children', () => {
        const fetchData = vi.fn()

        render(<DataCard
            title={"test"}
            dataCount={0}
            children={
                <div>test</div>
            }
            limitEnabled={false}
            fetchData={fetchData}/>)

        expect(screen.getByText(/no data available/i)).not.toBeNull
        expect(screen.getByText(/test 1 hour/i)).not.toBeNull
        expect(screen.getByText(/No data available/i)).not.toBeNull
    })

    it('should render a data card with one child', () => {
        const fetchData = vi.fn()

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

    it('should render a data card with a display period', async () => {
        const fetchData = vi.fn()
        const user = userEvent.setup();

        render(<DataCard
            title={"test"}
            dataCount={0}
            children={
                <div>test</div>
            }
            limitEnabled={false}
            fetchData={fetchData}/>)

        const input = screen.getByRole('spinbutton') as HTMLInputElement;
        fireEvent.change(input, {target: {value: '10'}});

        const menu = screen.getByRole('menu');
        const dayItem = within(menu).getByRole('menuitem', {name: 'd'});
        await user.click(dayItem);

        await waitFor(() => {
            expect(screen.getByText(/test 10 days/i)).toBeTruthy();
        });
    })
})