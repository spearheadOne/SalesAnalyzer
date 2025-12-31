output "sales_dashboard_url" {
  value = "http://${aws_lb.sales_dashboard.dns_name}"
}