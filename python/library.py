import csv   #Used to read/write CSV files
import os   #Used to handle file paths
import re  # Used for validation using patterns (regex) (like checking Book ID, Student ID)
from collections import Counter   
from datetime import datetime   

BOOK_FILE = "data/book.csv"
STUDENT_FILE = "data/student.csv"
TRANSACTION_FILE = "data/transaction.csv"
DATE_PATTERN = '%d/%m%Y'


def ensure_files():
    os.makedirs('data', exist_ok=True)
    files = {
        BOOK_FILE: ['book_id', 'title', 'isbn13', 'author', 'copies', 'availability', 'price'],
        STUDENT_FILE: ['student_id', 'first_name'],
        TRANSACTION_FILE: ['date', 'book_id', 'student_id', 'type'],
    }
    for path, header in files.items():
        if not os.path.exists(path):
            with open (path, 'w', newline='', encoding='utf-8') as f:
                writer = csv.writer(f)
                writer.writerow(header)



def read_csv(path):
    with open (path, 'r', newline='', encoding='utf-8') as f:
        return list(csv.DictReader(f))
    

def write_csv(path, rows, fieldnames):
    with open(path, 'w', newline='', encoding='utf-8') as f:
        writer = csv.DictWriter(f, fieldnames= fieldnames)
        writer.writeheader()
        writer.writerows(rows)


def append_csv(path, row, fieldnames):
    file_exists = os.path.exists(path)
    with open(path, 'a', newline='', encoding='utf-8')as f:
        writer = csv.DictWriter(f, fieldnames=fieldnames)
        if not file_exists:
            writer.writeheader()
        writer.writerow(row)


# function to validate input 
def is_letter_only(value, max_length):
    return bool(re.fullmatch(r'[A-Za-z]{1,' + str(max_length) + r'}', value.strip()))


 




ensure_files()