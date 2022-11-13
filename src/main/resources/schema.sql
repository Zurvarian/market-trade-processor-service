CREATE TABLE trade_volume
(
    composed_key  VARCHAR(30) PRIMARY KEY,
    period_point  DATETIME,
    currency_from VARCHAR(4),
    currency_to   VARCHAR(4),
    volume_count  INTEGER
);
