import {type PeriodUnit, usePeriodStore} from "../store/periodStore.ts";

export default function Period() {

    const units = usePeriodStore((state) => state.units);
    const period = usePeriodStore((state) => state.period);
    const setPeriod = usePeriodStore((state) => state.setPeriod);

    const value = parseInt(period, 10) || 1;
    const unit = (period.replace(/\d+/g, '') as PeriodUnit) || 'h';

    return (
            <div className="d-flex align-items-center gap-2">
                <input type="number"
                       className="form-control form-control-sm w-auto"
                       min={1} value={value}
                       onChange={(e) => setPeriod(parseInt(e.target.value, 10), unit)}
                />

                <div className="dropdown">
                    <button className="btn btn-secondary btn-sm dropdown-toggle"
                            type="button"
                            id="dropdownMenuButton1"
                            data-bs-toggle="dropdown"
                            aria-expanded="false">
                        {unit}
                    </button>
                    <ul className="dropdown-menu">
                        {units.map((u) => (
                            <li key={u}>
                                <a className="dropdown-item" href="#"
                                   onClick={() => setPeriod(value, u)}>
                                    {u}
                                </a>
                            </li>
                        ))}
                    </ul>
                </div>
            </div>
    )
}