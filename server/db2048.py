import pandas as pd
import logging
from IPython.display import display, HTML


def get_all_tables(db):
    with db.cursor() as c:
        c.execute("show tables")
        for table in c.fetchall():
            yield table[0]
            
def drop_table(db, name):
    cmd = "DROP TABLE {name};".format(name=name)
    with db.cursor() as c:
        c.execute(cmd)
            
def drop_all_tables(db):
    tables = list(get_all_tables(db))
    logging.info("Drop all tables, there were {} tables: {}".format(len(tables), tables))
    for table in tables:
        drop_table(db, table) 

def create_all_tables(db):
    with db.cursor() as c:
        c.execute('''
        CREATE TABLE users (
            USER_ID INT PRIMARY KEY,
            NAME CHAR(255)
        );

        CREATE TABLE versions (
            USER_ID INT PRIMARY KEY,
            VERSION INT
        );

        CREATE TABLE scores (
            PLAY_ID INT PRIMARY KEY,
            USER_ID INT,
            SCORE FLOAT
        );
        ''')

        
def show_table(db, name):
    display(HTML("<h2> {name} </h2>".format(name=name)))
    display(pd.read_sql_query("SELECT * FROM {name};".format(name=name), db))
    
def show_all_tables(db):
    tables = list(get_all_tables(db))
    display(HTML("Show all {} tables: {}".format(len(tables), tables)))
    for table in tables:
        show_table(db, table)
        
def read_all_from_cursor(c):
    res = []
    while True:
        res.append(list(c.fetchall()))
        if not c.nextset():
            break
    return res


def get_user(db, name):
    with db.cursor() as c:
        cmd = 'SELECT USER_ID FROM users WHERE NAME = "{user_name}";'.format(user_name=name)
        c.execute(cmd)
        users = read_all_from_cursor(c)[0]
        if len(users) == 0:
            return None
        if len(users) > 1:
            raise Exception("Too many users")
        return users[0][0]
    
def add_user(db, name):
    with db.cursor() as c:
        c.execute("""
SELECT USER_ID FROM users WHERE NAME = '{user_name}' LIMIT 1;
        """.format(user_name=name))
        res = read_all_from_cursor(c)[0]
        if len(res) == 1:
            return (res[0][0], False)
        else:
            c.execute("""
START TRANSACTION;
SET @NEW_USER_ID = (SELECT MAX(USER_ID) FROM users) + 1;
SET @NEW_USER_ID = COALESCE(@NEW_USER_ID, 0);
INSERT INTO users VALUES (@NEW_USER_ID, "{user_name}");
INSERT INTO versions VALUES (@NEW_USER_ID, 0);
COMMIT;
SELECT * FROM users WHERE USER_ID = @NEW_USER_ID;
            """.format(user_name=name))
            res = read_all_from_cursor(c)[-1]
            return (res[0][0], True)

        
def get_current_version(db, user_id):
    with db.cursor() as c:
        c.execute("""
SELECT VERSION FROM versions WHERE USER_ID = {user_id} LIMIT 1;
        """.format(user_id=user_id))
        res = read_all_from_cursor(c)[0]
        assert len(res) == 1, "No such user"
        return res[0][0]
    

def add_play(db, user_id, score, version):
    with db.cursor() as c:
        c.execute("""
START TRANSACTION;
SELECT VERSION FROM versions WHERE USER_ID = {user_id} LIMIT 1;
        """.format(user_id=user_id))
        res = read_all_from_cursor(c)[-1]
        assert len(res) == 1, "No such user"
        if res[0][0] != version:
            return (None, False)
        c.execute("""
SET @NEW_PLAY_ID = (SELECT MAX(PLAY_ID) FROM scores) + 1;
SET @NEW_PLAY_ID = COALESCE(@NEW_PLAY_ID, 0);
INSERT INTO scores VALUES (@NEW_PLAY_ID, {user_id}, {score});
REPLACE INTO versions VALUES ({user_id}, {new_version});
SELECT * FROM scores WHERE PLAY_ID = @NEW_PLAY_ID;
SELECT * FROM versions WHERE USER_ID = {user_id};
COMMIT;
        """.format(user_id=user_id, score=score, new_version=version + 1))
        res = read_all_from_cursor(c)[-3]
        return (res[0][0], True)
    
    
def get_rating(db, limit):
    return pd.read_sql_query('''
SELECT NAME, SCORE 
FROM users INNER JOIN scores ON users.USER_ID = scores.USER_ID 
ORDER BY SCORE DESC LIMIT {limit};
    '''.format(limit=limit), db)