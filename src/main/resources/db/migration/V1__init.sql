CREATE TABLE transactions
(
    hash           VARBINARY(32) PRIMARY KEY,
    status         VARCHAR(9)    NOT NULl,
    version        INT UNSIGNED  NOT NULl,
    blockType      VARCHAR(7)    NOT NULL,
    blockSubType   VARCHAR(7),
    accountVersion INT UNSIGNED,
    publicKey      VARBINARY(32),
    previous       VARBINARY(32),
    representative VARBINARY(32),
    balance        DECIMAL(39) UNSIGNED,
    link           VARBINARY(32),
    height         INT UNSIGNED,
    signature      VARBINARY(64) NOT NULL,
    work           VARBINARY(8)  NOT NULL,
    createdAt      TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updatedAt      TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);

CREATE TABLE pending_credit
(
    hash      VARBINARY(32) PRIMARY KEY,
    amount    BIGINT UNSIGNED NOT NULL,
    createdAt TIMESTAMP DEFAULT CURRENT_TIMESTAMP
)