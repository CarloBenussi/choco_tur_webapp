CREATE TABLE user_login_info(
    USER_ID BIGSERIAL PRIMARY KEY,
    EMAIL_ADDRESS VARCHAR(100) NOT NULL,
    PASSWORD VARCHAR(500) UNIQUE,
    EMAIL_VERIFICATION_NUMBER CHAR(6),
    EMAIL_VERIFICATION_NUMBER_EXPIRATION_TIME TIMESTAMP,
    EMAIL_VALIDATION_STATUS BOOLEAN,
    PASSWORD_RESET_TOKEN VARCHAR(100),
    PASSWORD_RESET_TOKEN_EXPIRATION_TIME TIMESTAMP,
    EXTERNAL_PROVIDER_ID INTEGER
);

CREATE TABLE external_providers(
    EXTERNAL_PROVIDER_ID BIGSERIAL PRIMARY KEY,
    EXTERNAL_PROVIDER_NAME VARCHAR(50) NOT NULL,
    EXTERNAL_PROVIDER_ENDPOINT VARCHAR(200) NOT NULL
);

CREATE TABLE user_account(
    USER_ID BIGSERIAL PRIMARY KEY,
    GENDER CHAR(1),
    BIRTH_DATE DATE,
    SELECTED_LANGUAGE CHAR(3),
    CAMERA_POSITION_LAT FLOAT,
    CAMERA_POSITION_LONG FLOAT
);

CREATE TABLE user_available_tours(
    USER_ID INTEGER,
    TOUR_ID INTEGER
);

CREATE TABLE user_available_tastings(
    USER_ID INTEGER,
    TOUR_ID INTEGER,
    TASTING_ID INTEGER,
    TASTING_TOKEN VARCHAR(100) NOT NULL
);

CREATE TABLE user_active_tour(
    USER_ID BIGSERIAL PRIMARY KEY,
    TOUR_ID INTEGER,
    TOUR_START_TIME TIMESTAMP NOT NULL,
    TOUR_NEXT_STOP_ID INTEGER,
    TOUR_NEXT_STOP_PAGE_INDEX INTEGER NOT NULL
);

CREATE TABLE tours(
    TOUR_ID BIGSERIAL PRIMARY KEY,
    TOUR_NAME VARCHAR(30) UNIQUE NOT NULL,
    COST_EUROS FLOAT NOT NULL,
    LENGTH_KM FLOAT NOT NULL,
    AVG_DURATION TIME NOT NULL,
    DESCRIPTION TEXT NOT NULL,
    STOPS_COUNT INTEGER NOT NULL,
    TASTINGS_COUNT INTEGER NOT NULL,
    IMAGE_ID INTEGER
);

CREATE TABLE tour_stops(
    TOUR_ID INTEGER,
    STOP_ID INTEGER,
    STOP_INDEX INTEGER NOT NULL
);

CREATE TABLE stops(
    STOP_ID BIGSERIAL PRIMARY KEY,
    STOP_NAME VARCHAR(30) UNIQUE NOT NULL,
    LATITUDE FLOAT NOT NULL,
    LONGITUDE FLOAT NOT NULL,
    DESCRIPTION TEXT NOT NULL,
    IMAGE_ID INTEGER
);

CREATE TABLE stop_tastings(
    STOP_ID INTEGER,
    TASTING_ID INTEGER
);

CREATE TABLE tastings(
    TASTING_ID BIGSERIAL PRIMARY KEY,
    DESCRIPTION TEXT NOT NULL,
    INGREDIENTS TEXT NOT NULL,
    ALLERGIES TEXT,
    IMAGE_ID INTEGER
);

CREATE TABLE stop_stories(
    STOP_ID INTEGER
);

CREATE TABLE images(
    IMAGE_ID BIGSERIAL PRIMARY KEY,
    IMAGE_LABEL TEXT,
    IMAGE_DATA BLOB NOT NULL
);