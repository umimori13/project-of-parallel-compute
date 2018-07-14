import tarfile
import gzip
import os
import csv
import operator
import pandas
import time

time_start=time.time()
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

print('task 0 done')
title  = ['Timestamp', 'Response',    'IOType',  'LUN', 'Offset',  'Size']
csv_R = open('R_temp.csv','a',newline='')
writer_R = csv.writer(csv_R)
writer_R.writerow(title)
csv_R.close()
csv_W = open('W_temp.csv','a',newline='')
writer_W = csv.writer(csv_W)
writer_W.writerow(title)
csv_W.close()

## TASK 1 ##
for name in names:
    if name == 'MD5.txt' or name == 'README.txt':
        pass
    else:    
        csv_reader = open(name.replace(".gz", ""),'r')
        csv_file = csv.reader(csv_reader)

        
        data_R = []
        data_W = []

        for i in csv_file:
            if i[2] == 'R':
                data_R.append(i)
            elif i[2] == 'W':
                data_W.append(i)

        csv_reader.close()

        csv_R = open('R_temp.csv','a',newline='')
        writer_R = csv.writer(csv_R)
        # writer_R.writerow(title[0])
        for i in data_R:
            writer_R.writerow(i)
        csv_R.close()


        csv_W = open('W_temp.csv','a',newline='')
        writer_W = csv.writer(csv_W)
        # writer_W.writerow(title[0])
        for i in data_W:
            writer_W.writerow(i)
        csv_W.close()

# csv_reader = open('2016022207-LUN1.csv','r')
# csv_file = csv.reader(csv_reader)

# title  = []
# data_R = []
# data_W = []

# for i in csv_file:
    # print(i)
#     if i[2] == 'R':
#         data_R.append(i)
#     elif i[2] == 'W':
#         data_W.append(i)
#     else:
#         title.append(i)

# csv_reader.close()

# csv_R = open('R_temp.csv','a',newline='')
# writer_R = csv.writer(csv_R)
# # writer_R.writerow(title[0])
# for i in data_R:
#     i = i[:5]+[int(i[5])]
#     # print(i)
#     writer_R.writerow(i)
# csv_R.close()


# csv_W = open('W_temp.csv','a',newline='')
# writer_W = csv.writer(csv_W)
# # writer_W.writerow(title[0])
# for i in data_W:
#     # if i[0] == 'Timestamp':
#     print(i)
#     writer_W.writerow(i)
# csv_W.close()
# print('task 1 done')

## TASK 2 ##
# first sort the Size , then sort the Timestamp
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

#temp to use pandas to sort and write
temp_file = pandas.read_csv('2016022207-LUN1.csv',low_memory=False,dtype = {'Timestamp':object,'Response':object,'Offset':object})
# for temp_file in pandas.read_csv('R_temp.csv',chunksize=10**6,low_memory=False):
    # temp_file.head()

# temp_file =temp_file.infer_objects()
# temp_file[['Size']] = temp_file[['Size']].apply(pandas.to_numeric,errors='ignore')
# temp_file[['Size']] = temp_file[['Size']].astype(float)
# print(temp_file)
x = temp_file['Timestamp'].str.split('.',expand = True)
# print('x',x)
# print(pandas.concat([temp_file,x],axis = 1))
# temp_file.round({'Timestamp':30})
# print(temp_file.head())
temp_file =temp_file.sort_values(by=['Size','0','1'])
temp_file = pandas.concat([temp_file,x],axis = 1)
# print(temp_file.columns)
temp_file = temp_file.drop(columns = [0,1])
# print(temp_file.head())
temp_file.to_csv("R.csv",index=False)
##temp done

# temp_read = open('R_temp.csv','r')
# temp_file = csv.reader(temp_read)

# title = []
# data = []
# with open('R_temp.csv','r') as f:
#     temp_file = csv.reader(f)
#     for i in f:
#         # print(i)
#         if i[0] == 'Timestamp':
#             title.append(i)
#         else:
#             data.append(i)

#     sortedlist = sorted(data, key = lambda x: (int(x[5]), float(x[0])))

# with open('R.csv', 'w', newline = '') as f:
#     fileWriter = csv.writer(f, delimiter=',')
#     fileWriter.writerow(title[0])
#     for row in sortedlist:
#         fileWriter.writerow(row)

# temp_read.close()
# f.close()

# os.remove('R_temp.csv')
# os.remove('W_temp.csv')

print('task 2 done')

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
print('task 3 done')

time_end=time.time()
print('totally cost',time_end-time_start)
