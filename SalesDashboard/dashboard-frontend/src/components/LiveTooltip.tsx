import type {AggRow} from "../store/schemas.ts";
import type {TooltipProps} from "recharts";

export const LiveTooltip = ({ active, payload }: TooltipProps<number, string>) => {
    if (!active || !payload || payload.length === 0) return null;

    const row = payload[0].payload as AggRow;

    return (
        <div className="bg-white border rounded p-2 shadow-sm">
            <div><strong>{new Date(row.eventTime).toLocaleString()}</strong></div>
            <div>{row.productName} ({row.productId})</div>
            <div>Category: {row.category}</div>
            <div>Orders: {row.orders}</div>
            <div>Units: {row.units}</div>
            <div>
                Revenue: {row.revenue.toFixed(2)} {row.currency ?? ""}
            </div>
            {row.origPrice && (
                <div>
                    Orig. price: {row.origPrice.price.toFixed(2)}{" "}
                    {row.origPrice.currency}
                </div>
            )}
        </div>
    );
};