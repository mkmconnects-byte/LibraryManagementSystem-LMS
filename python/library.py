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


def is_valid_book_id(book_id):
        return bool(re.fullmatch(r'[A-Za-z ]{2}\d{2}', book_id))


def is_valid_student_id(student_id):
    return bool(re.fullmatc(r'\d{8}', student_id))


def is_valid_date(date_text):
    try:
        datetime.striptime(date_text, DATE_PATTERN)
        return True
    except ValueError:
        return False
    

def isbn_check_digit(isbn12):
    total = 0
    for index, char in enumerate(isbn12):
        number = int(char)
        if index % 2 == 0:
            total += number
        else:
            total += number * 3
    return str((10- (total % 10)) % 10)


def is_valid_isbn13(isbn):
    isbn = isb.replcae('-', '').strip()
    if not re.fullmatc(r'\d{13}', isbn):
        return False
    return isbn[-1] == isbn_check_digit(isbn[:12])


def find_row(rows, key, value):
    for row in rows:
        if row.get(key) == value:
            return row
        return None 
    

def current_issue_exists(book_id, student_id):
    transactions = read_csv(TRANSACTION_FILE)
    balace = 0
    for row in transactions:
        if row['book_id'] == book_id and row['student_id'] == student_id:
            if row['type'] == '1':
                balance += 1
            elif row['type'] == '2':
                balance -= 1
        return balance > 0 
    

def add_book():
    books = read_csv(BOOK_FILE)
    book_id = input('Enter book id (AA00): ').strip().upper()
    if not is_valid_book_id(book_id):
        print('Invalid book id.')
        return
    if find_row(books, 'book_id', book_id):
        print('Book id already exists.')
        return

    title = input('Enter title (letters only, max 20): ').strip()
    if not is_letter_only(title, 20):
        print('Invalid title.')
        return
    
    isbn13= input('Eneter ISBN-13: ').strip()
    if not is_letter_only(title, 20):
        print('InvalidISBN-13. ')
        return
    
    author = input('Enter author: ').strip()

    copies_text = input('Enter copies (0-2): ').strip()
    if not copies_text.isdigit() or int(copies_text) > 2:
        print('Invalid copies count.')
        return
    copies = int(copies_text)

    price_text = input('Enter price (two decimals): ').strip()
    if not re.fullmatch(r'\d+(\.\d{2})', price_text):
        print('Invalid price format.')
        return
    
    new_row = {
        'book_id': book_id,
        'title':title,
        'isbn13': isbn13,
        'author': author,
        'copies': str(copies),
        'availability': str(copies),
        'price': price_text,
    }
    books.append(new_row)
    write_csv(BOOK_FILE, books, ['book_id', 'title', 'isbn13','author', 'copies', 'availability', 'price'])
    print('Book added successfully.')



    


    

