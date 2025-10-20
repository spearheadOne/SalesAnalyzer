import {type StoreApi, type UseBoundStore} from "zustand";
import {create} from "zustand/react";


export type PeriodUnit = 'm' | 'h' | 'd' | 's'

export interface PeriodState {

    units: readonly PeriodUnit[],

    period: string,

    setPeriod: (value: number, unit: PeriodUnit) => void,

    getDisplayPeriod: () => string,
}

export function makePeriodStore(): UseBoundStore<StoreApi<PeriodState>> {
    return create<PeriodState>((set, get) => ({
        units: ['m', 'h', 'd', 's'] as const,
        period: '1h',

        setPeriod: (value: number, unit: PeriodUnit) => {set({ period: `${value}${unit}`})},

        getDisplayPeriod(): string {
            const period = get().period;
            const unit = period.slice(-1) as PeriodUnit;
            const value = parseInt(period.slice(0, -1));

            const unitMap: Record<PeriodUnit, string> = {
                'm': 'minute',
                'h': 'hour',
                'd': 'day',
                's': 'second'
            }

            const unitName = unitMap[unit];

            return `${value} ${unitName}${value > 1 ? 's' : ''}`;
        }

    }))
}