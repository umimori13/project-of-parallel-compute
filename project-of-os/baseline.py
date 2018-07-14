import tarfile
import gzip
import os
import csv
import operator

## TASK 0 ##
# untar the file systor17-01.tar into a file with the same name
#os.mkdir('systor17-01')
tar = tarfile.open('systor17-01.tar')
names = tar.getnames()
# for name in names:
#     tar.extract(name, 'systor17-01/')
#print(names) ===> XXX.gz
tar.extractall()
tar.close()

# ungza the file XXX.csx.gz and get the csv file
def un_gz(file_name):
    """ungz zip file"""
    f_name = file_name.replace(".gz", "")
    g_file = gzip.GzipFile(file_name)
    open(f_name, "wb").write(g_file.read())
    g_file.close()

for name in names:
    if name == 'MD5.txt' or name == 'README.txt':
        pass
    else:
        un_gz(name)

## TASK 1 ##
for name in names:
    if name == 'MD5.txt' or name == 'README.txt':
        pass
    else:    
        csv_reader = open(name.replace(".gz", ""),'r')
        csv_file = csv.reader(csv_reader)

        title  = []
        data_R = []
        data_W = []

        for i in csv_file:
            if i[2] == 'R':
                data_R.append(i)
            elif i[2] == 'W':
                data_W.append(i)
            else:
                title.append(i)

        csv_reader.close()

        csv_R = open('R_temp.csv','a',newline='')
        writer_R = csv.writer(csv_R)
        writer_R.writerow(title[0])
        for i in data_R:
            writer_R.writerow(i)
        csv_R.close()


        csv_W = open('W_temp.csv','a',newline='')
        writer_W = csv.writer(csv_W)
        writer_W.writerow(title[0])
        for i in data_W:
            writer_W.writerow(i)
        csv_W.close()

## TASK 2 ##
## first sort the Size , then sort the Timestamp
temp_read = open('W_temp.csv','r')
temp_file = csv.reader(temp_read)
title = []
data = []
for i in temp_file:
    if i[0] == 'Timestamp':
        title.append(i)
    else:
        data.append(i)

sortedlist = sorted(data, key = lambda x: (int(x[5]), float(x[0])))

with open('W.csv', 'w', newline = '') as f:
    fileWriter = csv.writer(f, delimiter=',')
    fileWriter.writerow(title[0])
    for row in sortedlist:
        fileWriter.writerow(row)

temp_read.close()
f.close()

temp_read = open('R_temp.csv','r')
temp_file = csv.reader(temp_read)
title = []
data = []
for i in temp_file:
    if i[0] == 'Timestamp':
        title.append(i)
    else:
        data.append(i)

sortedlist = sorted(data, key = lambda x: (int(x[5]), float(x[0])))

with open('R.csv', 'w', newline = '') as f:
    fileWriter = csv.writer(f, delimiter=',')
    fileWriter.writerow(title[0])
    for row in sortedlist:
        fileWriter.writerow(row)

temp_read.close()
f.close()

# os.remove('R_temp.csv')
# os.remove('W_temp.csv')

## Task 3
## count the number of different size
file_W = open('W.csv','r')
data = csv.reader(file_W)
analysis = []
t = 0
size = -1
for i in data:
    if i[5] == 'Size':
        pass
    elif i[5] != size:
        analysis.append([size,t])
        size = i[5]
        t = 1
    else:
        t = t + 1
analysis.append([size,t])

file_W.close()

csv_W = open('W.csv','a',newline='')
writer_W = csv.writer(csv_W)
writer_W.writerow([])
for i in analysis:
    if i[0] != -1:
        writer_W.writerow(i)
csv_W.close()

file_R = open('R.csv','r')
data = csv.reader(file_R)
analysis = []
t = 0
size = -1
for i in data:
    if i[5] == 'Size':
        pass
    elif i[5] != size:
        analysis.append([size,t])
        size = i[5]
        t = 1
    else:
        t = t + 1
analysis.append([size,t])

file_R.close()

csv_R = open('R.csv','a',newline='')
writer_R = csv.writer(csv_R)
writer_R.writerow([])
for i in analysis:
    if i[0] != -1:
        writer_R.writerow(i)
csv_R.close()