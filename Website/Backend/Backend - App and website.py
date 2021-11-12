# -*- coding: utf-8 -*-
"""
Created on Thu Jun 10 17:41:14 2021

@author: aksha
"""


from flask import Flask, request, jsonify, render_template
import joblib
import traceback, os
import pandas as pd
import jinja2
import mysql.connector
from androguard.core.bytecodes.apk import APK
from time import time


'''Initializing FLASK and index.html'''
app = Flask(__name__)
template_dir = os.path.join(os.path.dirname(__file__), 'templates')
jinja_env = jinja2.Environment(loader=jinja2.FileSystemLoader(template_dir),autoescape=True)
app.jinja_env.filters['zip'] = zip


'''Initializing the "uploads" folder'''
uploads_dir = os.path.join(app.instance_path, 'uploads')
os.makedirs(uploads_dir, exist_ok = True)



'''Connecting to the MySQL server containing the database named naticus'''
mydb = mysql.connector.connect(
  host="localhost",
  user="akshay",
  password="Cstar_2033",
  database="naticus"
)

if mydb.is_connected():
    print("Connected to MySQL")
else:
    print("NOT CONNECTED")    

model = joblib.load("Naticus.pkl") # Load "model.pkl"
print ('Model loaded')
model_columns = joblib.load("Naticus_columns.pkl") # Load "model_columns.pkl"
print ('Model columns loaded')
PERMISSIONS = model_columns
v = len(PERMISSIONS)



'''Starting point of Web App, shows the "Home" page'''
@app.route('/')
def index():
   return render_template('index.html')


'''
This is the main part of the App.
It recieves the uploaded apk file, saves it in the "uploads folder",
Fetches the apk from the "uploads folder", calls the "create_perm_vector" function,
calls the "classify" function, removes the uploaded file, and displays the result.
'''
@app.route('/upload', methods = ['POST'])
def upload_file():
   if request.method == 'POST':
       try:
           f = request.files['file']
           f.save(os.path.join(uploads_dir,f.filename))
           
           
           if(uploads_dir != None):
               apk_file_directory = uploads_dir
               if(not os.path.exists(apk_file_directory)):
                   print('%s does not exist' % apk_file_directory)
               else:
                   for root, dir, files in os.walk(apk_file_directory):
                       apk_file_list = [os.path.join(root, file_name) for file_name in files]
                      
               apk = apk_file_list.pop()
               
               
               start = time()
               
               apk_file_name, perms, perm_info, permission_vector = create_perm_vector(apk)
               result = classify(permission_vector)
               
               stop = time()
               print(str(stop - start) + " seconds")
               flag = save_to_app_db(apk_file_name, perms, result)
               print(flag)
               os.remove(uploads_dir+"\\"+f.filename)
               
               info = []
               category = []
               
               for element in perm_info:
                   for e in element:
                       info.append(e[0])
                       category.append(e[1])
                            
 
               features = {'name': apk_file_name,
                           'perms': perms,
                           'info': info,
                           'category': category,
                           'class': result}
               
               return render_template("info.html", features = features)          
       except:
           return jsonify({'trace': traceback.format_exc()})
   else:
      return jsonify({'Result': 'file not uploaded'})


'''
FOR APP
For getting classification result
'''

@app.route("/naticus", methods = ["POST"])
def parse_request_list():
    recieved_string = request.form['perm_list']

    permission_list = list(recieved_string.split(","))
    permission_list.pop()
    
    permission_vector = create_perm_vector_naticus(permission_list)
    
    result = classify(permission_vector)
    
    return result;


'''
Retieve use of permission
'''

@app.route("/perm_info", methods = ["POST"])
def parse_request_perm():
    response = "No details are available for this permission at present! ~ Try looking up online ~ Dangerous(Default) ~ "
    permission = request.form['perm']
    cursor = mydb.cursor()
    sql = "SELECT * FROM permissions WHERE perm_name = %s"
    val = (permission,)
    cursor.execute(sql,val)
    result = cursor.fetchall()
    if result != None:
        response = ""
        for value in result:
            for i in range(len(value)):
                response = response + value[i] + " ~ "
        return response
    else:
        return "No details are available for this permission at present! ~ Try looking up online ~ Dangerous(Default) ~ "
    

'''
FOR WEBSITE
This function takes the apk file as arguments, and creates a permission vector for the model
'''
def create_perm_vector(apk_file):
    try:
	    a = APK(apk_file)
    except:
        return None
    
    apk_file_name = a.get_app_name()
    perms = a.get_permissions()
    
    print(perms)
    
    perm_info = fetch_from_perms_db(perms)
    
    perm_vector = [] * v
    for permission in PERMISSIONS:
        hit = 1 if permission in perms else 0
        perm_vector.append(hit)
    
    return apk_file_name, perms, perm_info, list(perm_vector)


'''
FOR APP
This function passes the permission vector to the model,
and yeilds whether an app is benign or malicious.
'''

def create_perm_vector_naticus(permission_list):
    perm_vector = [] * v
    for permission in PERMISSIONS:
        hit = 1 if permission in permission_list else 0
        perm_vector.append(hit)
    
    return list(perm_vector)

'''
This function passes the permission vector to the model,
and yeilds whether an app is benign or malicious
'''
def classify(query):
    if model:
        query = [query]
        query = pd.DataFrame(query, columns = model_columns)
            
        classification = model.predict(query)
        
        if classification == 0:
            category = 'Benign'
        else:
            category = 'Malware'
        
        return category
        
    else:
        return ('No model here to use')


'''Inserting app information into the database table.'''
def save_to_app_db(apk_file_name, perms, result):
    perm_str = str(perms).strip('[]')
    try:
        cursor = mydb.cursor()
        sql = "SELECT app_name FROM applications WHERE app_name = (%s)"
        val = (apk_file_name,)
        cursor.execute(sql,val)
        rows = cursor.fetchall()
        if len(rows) < 1:
            cursor = mydb.cursor()
            sql = "INSERT INTO applications (app_name, app_perms, app_classification) VALUES (%s, %s, %s)"
            val = (apk_file_name, perm_str, result)
            cursor.execute(sql,val)
            mydb.commit()
            return "RECORD INSERTED SUCCESSFULLY!"            
        else:
            cursor = mydb.cursor()
            sql = "UPDATE applications SET app_perms = (%s), app_classification = (%s) WHERE app_name = (%s)"
            val = (perm_str, result, apk_file_name)
            cursor.execute(sql,val)
            mydb.commit()
            return "RECORD SUCCESSFULLY UPDATED!"
    except mysql.connector.Error as err:
        return "Something went wrong: {}".format(err)


'''
Fetches information of a permission into the database
'''

def fetch_from_perms_db(perms):
    perm_info = []
    for perm in perms:
        try:
            cursor = mydb.cursor()
            sql = "SELECT perm_about,perm_category FROM permissions WHERE perm_name = (%s)"
            val = (perm,)
            cursor.execute(sql,val)
            rows = cursor.fetchall() 
            if len(rows) < 1:
                rows = [('NA', 'NA')]
            perm_info.append(rows)
        except mysql.connector.Error as err:
            return "Something went wrong: {}".format(err)
        
    return perm_info

'''
The main function initializes the ports for the server, loads the model and variables, and runs the server.
'''
if __name__ == '__main__':    
    try:
        port = int(sys.argv[1]) # This is for a command-line input
    except:
        port = 5000 # If you don't provide any port the port will be set to 12345

#    app.run(host='172.31.203.233',port=port)  
    website_url = 'www.permissioneducator.net:5000'
    app.config['SERVER_NAME'] = website_url
    app.run()    



