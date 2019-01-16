#!/usr/bin/env python3
# coding: utf-8

# In[1]:


import MySQLdb
import pandas as pd
import db2048
import traceback
import json


# In[2]:


db = MySQLdb.connect("localhost", "u2048", "u2048passord", init_command="use testDB")
db.encoding = "utf-8"

# In[ ]:





# In[3]:


#!sudo pip3 install flask


# In[4]:


def transform_rating(df):
    n = len(df)
    data = df.to_dict()
    rating = []
    for i in range(n):
        rating.append({
            "place": i + 1,
            "name": data["NAME"][i],
            "score": data["SCORE"][i]
        })
    return rating


# In[ ]:

import logging
import flask

app = flask.Flask(__name__)

@app.route('/rating')
def rating():
    limit = int(flask.request.args.get('limit', 5))
    if bool(flask.request.args.get('json', "")):
        return json.dumps(transform_rating(db2048.get_rating(db, limit=limit)))
    else:
        return db2048.get_rating(db, limit=limit).to_html(index=False)

@app.route('/add_user')
def add_user():
    user_name = "no name :("
    try:
        user_name = str(flask.request.args['name'])
    except Exception:
        return traceback.format_exc(), 400
    try:
        user_id, new = db2048.add_user(db, name=user_name)
        return json.dumps({"success": True, "user_id": user_id, "new": new}), 200
    except Exception:
        logging.exception("Can't add user '{}': ".format(user_name))
        return traceback.format_exc(), 500

@app.route('/get_current_version')
def get_current_version():
    try:
        user_id = int(flask.request.args['user_id'])
    except Exception:
        return traceback.format_exc(), 400
    try:
        version = db2048.get_current_version(db, user_id=user_id)
        return json.dumps({"success": True, "version": version}), 200
    except Exception:
        return traceback.format_exc(), 500
    
@app.route('/add_play')
def add_play():
    try:
        user_id = int(flask.request.args['user_id'])
        score = float(flask.request.args['score'])
        version = int(flask.request.args['version'])
    except Exception:
        return traceback.format_exc(), 400
    try:
        play_id, success = db2048.add_play(db, user_id=user_id, score=score, version=version)
        return json.dumps({"success": success, "play_id": play_id}), 200
    except Exception:
        return traceback.format_exc(), 500


@app.route('/')
def index():
    return("Hello World!")


app.run(port=8890, host="0.0.0.0")
