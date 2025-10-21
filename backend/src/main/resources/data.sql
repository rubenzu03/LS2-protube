-- Drop tables if they exist (in correct order due to foreign key constraints)
DROP TABLE IF EXISTS videos_tags;
DROP TABLE IF EXISTS videos_categories;
DROP TABLE IF EXISTS comments;
DROP TABLE IF EXISTS videos;
DROP TABLE IF EXISTS tags;
DROP TABLE IF EXISTS categories;
DROP TABLE IF EXISTS users;

-- Create Users table
CREATE TABLE users (
    username VARCHAR(255) PRIMARY KEY,
    id BIGINT,
    auth_id VARCHAR(255),
    password VARCHAR(255)
);

-- Create Categories table
CREATE TABLE categories (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    name VARCHAR(255) NOT NULL
);

-- Create Tags table
CREATE TABLE tags (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    name VARCHAR(255) NOT NULL
);

-- Create Videos table
CREATE TABLE videos (
    id BIGINT PRIMARY KEY,
    title VARCHAR(500) NOT NULL,
    width FLOAT NOT NULL,
    height FLOAT NOT NULL,
    duration FLOAT NOT NULL,
    description TEXT,
    video_url VARCHAR(500) NOT NULL,
    user_id VARCHAR(255),
    FOREIGN KEY (user_id) REFERENCES users(username)
);

-- Create Comments table
CREATE TABLE comments (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    text TEXT NOT NULL,
    video_id BIGINT,
    user_id VARCHAR(255),
    FOREIGN KEY (video_id) REFERENCES videos(id),
    FOREIGN KEY (user_id) REFERENCES users(username)
);

-- Create junction table for Videos and Categories (many-to-many)
CREATE TABLE videos_categories (
    video_id BIGINT,
    category_id BIGINT,
    PRIMARY KEY (video_id, category_id),
    FOREIGN KEY (video_id) REFERENCES videos(id),
    FOREIGN KEY (category_id) REFERENCES categories(id)
);

-- Create junction table for Videos and Tags (many-to-many)
CREATE TABLE videos_tags (
    video_id BIGINT,
    tag_id BIGINT,
    PRIMARY KEY (video_id, tag_id),
    FOREIGN KEY (video_id) REFERENCES videos(id),
    FOREIGN KEY (tag_id) REFERENCES tags(id)
);

-- Populate Users (username is now primary key)
INSERT INTO users (username, id) VALUES ('Bruno Mars', 1);
INSERT INTO users (username, id) VALUES ('Mark Ronson', 2);
INSERT INTO users (username, id) VALUES ('Meat Loaf', 3);
INSERT INTO users (username, id) VALUES ('Taylor Swift', 4);
INSERT INTO users (username, id) VALUES ('Bonnie Tyler', 5);
INSERT INTO users (username, id) VALUES ('Ed Sheeran', 6);
INSERT INTO users (username, id) VALUES ('Danny Ocean', 7);
INSERT INTO users (username, id) VALUES ('Songkick', 8);
INSERT INTO users (username, id) VALUES ('Rudimental', 9);
INSERT INTO users (username, id) VALUES ('jlopeza', 10);

-- Populate Videos (use usernames as user_id to match new PK)
INSERT INTO videos (id, title, width, height, duration, description, video_url, user_id) VALUES
(0, 'Bruno Mars - 24K Magic (Official Music Video)', 1920, 1080, 27.0, 'The official music video for Bruno Mars'' "24K Magic" from the album ''24K Magic''.', '/api/videos/stream/0', 'Bruno Mars');

INSERT INTO videos (id, title, width, height, duration, description, video_url, user_id) VALUES
(1, 'Mark Ronson - Uptown Funk (Official Video) ft. Bruno Mars', 1920, 1080, 38.0, 'Official Video for Uptown Funk by Mark Ronson ft. Bruno Mars', '/api/videos/stream/1', 'Mark Ronson');

INSERT INTO videos (id, title, width, height, duration, description, video_url, user_id) VALUES
(2, 'Meat Loaf - I''d Do Anything For Love (But I Won''t Do That) (Official Music Video)', 1920, 1080, 72.0, 'REMASTERED IN HD! Official video of Meat Loaf performing I''d Do Anything For Love (But I Won''t Do That) from the album Bat Out of Hell II.', '/api/videos/stream/2', 'Meat Loaf');

INSERT INTO videos (id, title, width, height, duration, description, video_url, user_id) VALUES
(3, 'Taylor Swift - Shake It Off', 1920, 1080, 30.0, 'Official music video for Taylor Swift - Shake It Off', '/api/videos/stream/3', 'Taylor Swift');

INSERT INTO videos (id, title, width, height, duration, description, video_url, user_id) VALUES
(4, 'Bonnie Tyler - Total Eclipse of the Heart (Turn Around) (Official Video)', 1920, 1440, 29.0, 'Bonnie Tyler - Total Eclipse of the Heart (Turn Around) (Official Video)', '/api/videos/stream/4', 'Bonnie Tyler');

INSERT INTO videos (id, title, width, height, duration, description, video_url, user_id) VALUES
(5, 'Ed Sheeran - BLOW (with Chris Stapleton & Bruno Mars) [Official Music Video]', 1920, 1080, 21.0, 'The official music video for Ed Sheeran - BLOW (with Chris Stapleton & Bruno Mars)', '/api/videos/stream/5', 'Ed Sheeran');

INSERT INTO videos (id, title, width, height, duration, description, video_url, user_id) VALUES
(6, 'Danny Ocean - Me Rehúso (Official Audio)', 1920, 1080, 24.0, 'Official Audio "Me Rehúso" by Danny Ocean', '/api/videos/stream/6', 'Danny Ocean');

INSERT INTO videos (id, title, width, height, duration, description, video_url, user_id) VALUES
(7, 'Ed Sheeran - Beautiful People (feat. Khalid) [Official Music Video]', 1920, 1080, 28.0, 'The official music video for Ed Sheeran - Beautiful People (feat. Khalid)', '/api/videos/stream/7', 'Ed Sheeran');

INSERT INTO videos (id, title, width, height, duration, description, video_url, user_id) VALUES
(8, 'Ed Sheeran - You Need Me, I Don''t Need You | LIVE', 1920, 1080, 22.0, 'Ed Sheeran performs his song "You Need Me, I Don''t Need You" in an exclusive recording session live at Hinge Studios in Chicago, IL for The Live Room.', '/api/videos/stream/8', 'Songkick');

INSERT INTO videos (id, title, width, height, duration, description, video_url, user_id) VALUES
(9, 'Rudimental - Waiting All Night (ft. Ella Eyre) [Official Video]', 640, 360, 25.0, 'The official music video for Rudimental - Waiting All Night ft. Ella Eyre', '/api/videos/stream/9', 'Rudimental');

INSERT INTO videos (id, title, width, height, duration, description, video_url, user_id) VALUES
(10, 'Taylor Swift - Blank Space', 1920, 1080, 71.0, 'Official music video for Taylor Swift - Blank Space from the 1989 album', '/api/videos/stream/10', 'Taylor Swift');

INSERT INTO videos (id, title, width, height, duration, description, video_url, user_id) VALUES
(11, 'Video 11', 1920, 1080, 30.0, 'Video from store', '/api/videos/stream/11', 'Bruno Mars');

INSERT INTO videos (id, title, width, height, duration, description, video_url, user_id) VALUES
(12, 'Video 12', 1920, 1080, 30.0, 'Video from store', '/api/videos/stream/12', 'Bruno Mars');

INSERT INTO videos (id, title, width, height, duration, description, video_url, user_id) VALUES
(25, 'screenshott', 1920, 1080, 2.0, 'balblablablvsdvsdv s', '/api/videos/stream/25', 'jlopeza');

INSERT INTO videos (id, title, width, height, duration, description, video_url, user_id) VALUES
(95, 'Video 95', 1920, 1080, 30.0, 'Video from store', '/api/videos/stream/95', 'Bruno Mars');

INSERT INTO videos (id, title, width, height, duration, description, video_url, user_id) VALUES
(96, 'Video 96', 1920, 1080, 30.0, 'Video from store', '/api/videos/stream/96', 'Bruno Mars');

INSERT INTO videos (id, title, width, height, duration, description, video_url, user_id) VALUES
(97, 'Video 97', 1920, 1080, 30.0, 'Video from store', '/api/videos/stream/97', 'Bruno Mars');

INSERT INTO videos (id, title, width, height, duration, description, video_url, user_id) VALUES
(98, 'Video 98', 1920, 1080, 30.0, 'Video from store', '/api/videos/stream/98', 'Bruno Mars');

INSERT INTO videos (id, title, width, height, duration, description, video_url, user_id) VALUES
(99, 'Video 99', 1920, 1080, 30.0, 'Video from store', '/api/videos/stream/99', 'Bruno Mars');

INSERT INTO videos (id, title, width, height, duration, description, video_url, user_id) VALUES
(100, 'Video 100', 1920, 1080, 30.0, 'Video from store', '/api/videos/stream/100', 'Bruno Mars');

INSERT INTO videos (id, title, width, height, duration, description, video_url, user_id) VALUES
(101, 'Video 101', 1920, 1080, 30.0, 'Video from store', '/api/videos/stream/101', 'Bruno Mars');

INSERT INTO videos (id, title, width, height, duration, description, video_url, user_id) VALUES
(102, 'Video 102', 1920, 1080, 30.0, 'Video from store', '/api/videos/stream/102', 'Bruno Mars');

INSERT INTO videos (id, title, width, height, duration, description, video_url, user_id) VALUES
(103, 'Video 103', 1920, 1080, 30.0, 'Video from store', '/api/videos/stream/103', 'Bruno Mars');

INSERT INTO videos (id, title, width, height, duration, description, video_url, user_id) VALUES
(104, 'Video 104', 1920, 1080, 30.0, 'Video from store', '/api/videos/stream/104', 'Bruno Mars');

INSERT INTO videos (id, title, width, height, duration, description, video_url, user_id) VALUES
(105, 'Video 105', 1920, 1080, 30.0, 'Video from store', '/api/videos/stream/105', 'Bruno Mars');

