export function formatCurrency(n: number){
    return n.toLocaleString(undefined, {maximumFractionDigits: 2})
}