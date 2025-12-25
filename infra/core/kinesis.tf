resource "aws_kinesis_stream" "sales_stream" {
  name = "${var.kinesis_stream_name}-${var.environment}"

  stream_mode_details {
    stream_mode = "ON_DEMAND"
  }

  retention_period = 48

  shard_level_metrics = [
    "IncomingBytes",
    "IncomingRecords",
    "OutgoingBytes",
    "OutgoingRecords",
    "IteratorAgeMilliseconds",
    "ReadProvisionedThroughputExceeded",
    "WriteProvisionedThroughputExceeded"
  ]

  encryption_type = "KMS"
  kms_key_id      = "alias/aws/kinesis"
}