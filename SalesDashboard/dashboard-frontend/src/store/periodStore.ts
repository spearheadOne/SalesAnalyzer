import {create} from "zustand/react";


export type PeriodUnit = 'm' | 'h' | 'd' | 's'

export interface PeriodStore {

    units: readonly PeriodUnit[],

    period: string,

    setPeriod: (value: number, unit: PeriodUnit) => void,

    getDisplayPeriod: () => string,
}


export const usePeriodStore = create<PeriodStore>((set, get) => ({
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
