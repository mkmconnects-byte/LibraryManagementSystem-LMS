import csv   #Used to read/write CSV files
import os   #Used to handle file paths
import re  # Used for validation using patterns (regex) (like checking Book ID, Student ID)
from collections import Counter   
from datetime import datetime   

BOOK_FILE = "data/book.csv"
STUDENT_FILE = "data/student.csv"
TRANSACTION_FILE = "data/transaction.csv"
DATE_PATTERN = '%d/%m/%Y'


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


# as of a normal function we have to open the file , write and close it.
# but  with the use of "with" we can manually open the file, use it and close it.


def read_csv(path):
    with open (path, 'r', newline='', encoding='utf-8') as f:
        return list(csv.DictReader(f))
    
# newline='' → controls how line breaks are handled, without it we might get empty lines between rows.
# encoding → how text is stored/read in file, without it we might get unicodeDecodeError.


# if you have  rows = [
#  {'book_id': 'B01', 'title': 'Java'}
#   ]
# DictWriter knows that book_id -> first column and title -> second column
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
    return bool(re.fullmatch(r'[A-Za-z ]{1,' + str(max_length) + r'}', value.strip()))

def is_valid_book_id(book_id):
    return re.fullmatch(r'[A-Za-z]{2}\d{2}', book_id)

def is_valid_student_id(student_id):
    return bool(re.fullmatch(r'\d{8}', student_id))


def is_valid_date(date_text):
    try:
        datetime.strptime(date_text, DATE_PATTERN)
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
    isbn = isbn.replace('-', '').strip()
    if not re.fullmatch(r'\d{13}', isbn):
        return False
    return isbn[-1] == isbn_check_digit(isbn[:12])


def find_row(rows, key, value):
    for row in rows:
        if row.get(key) == value:
            return row
    return None
    

def current_issue_exists(book_id, student_id):
    transactions = read_csv(TRANSACTION_FILE)
    balance = 0
    for row in transactions:
        if row['book_id'] == book_id and row['student_id'] == student_id:
            if row['type'] == '1':
                balance += 1
            elif row['type'] == '2':
                balance -= 1
        return balance > 0 
    

def add_book():
    books = read_csv(BOOK_FILE)

    while True:
        book_id = input('Enter book id (AA00): ').strip().upper()
        if not is_valid_book_id(book_id):
            print('Invalid book id.')
        elif find_row(books, 'book_id', book_id):
            print('Book id already exists.')
        else:
            break

    while True:
        title = input('Enter title (letters only, max 20): ').strip()
        if not is_letter_only(title, 20):
            print('Invalid title.')
        else:
            break

    while True:
        isbn13 = input('Enter ISBN-13: ').strip()
        if not is_valid_isbn13(isbn13):
            print('Invalid ISBN-13.')
        else:
            break

    while True:
        author = input('Enter author (letters only, max 20): ').strip()
        if not is_letter_only(author, 20):
            print('Invalid author.')
        else:
            break

    while True:
        copies_text = input('Enter copies (0-2): ').strip()
        if not copies_text.isdigit() or not (0 <= int(copies_text) <= 2):
            print('Invalid copies count.')
        else:
            copies = int(copies_text)
            break


# the second line tells the price_text format
#\d = digit(0-9) and + means one r more
#\. means decimals point and\d{2} means exactly 2 digits
    while True:
        price_text = input('Enter price (two decimals): ').strip()
        if not re.fullmatch(r'\d+(\.\d{2})', price_text):
            print('Invalid price format.')
        else:
            break

    new_row = {
        'book_id': book_id,
        'title': title,
        'isbn13': isbn13,
        'author': author,
        'copies': str(copies),
        'availability': str(copies),
        'price': price_text,
    }

    books.append(new_row)
    write_csv(
        BOOK_FILE,
        books,
        ['book_id', 'title', 'isbn13', 'author', 'copies', 'availability', 'price']
    )

    print('Book added successfully.')


def view_books():
    books = read_csv(BOOK_FILE)
    if not books:
        print('No books found.')
        return
    print('\nBooks')  # breaking the line when booksis typed.
    print('-' * 80)  # nothing fancy just adding a line with 80 "-".
    for row in books:
        print(f"{row['book_id']} | {row['title']} | ISBN {row['isbn13']} | author {row['author']} | Copies {row['copies']} | Available {row['availability']} | Price {row['price'] }")


def search_books():
    books = read_csv(BOOK_FILE)

    while True:
        keyword = input('Enter the keyword (or Q to quit): ').strip().lower()

        if keyword == 'q':
            break

        matches = [row for row in books if keyword in row['title'].lower()]

        if not matches:
            print('No matching books found.')
        else:
            for row in matches:
                print(f"{row['book_id']} | {row['title']} | Available {row['availability']}")


def edit_book():
    books = read_csv(BOOK_FILE)

    while True:
        book_id = input('Enter book id to edit: ').strip().upper()
        book = find_row(books, 'book_id', book_id)

        if not book:
            print('Invalid book id.')
        else:
            break
    
    while True:
        new_title = input(f"New title [{book['title']}]: ").strip()

# this keeps the old title

        if not new_title:
            break  

        if not is_letter_only(new_title, 20):
            print('Invalid title.')
        else:
            book['title'] = new_title
            break
                
    
    while True:
        new_author = input(f"New author [{book['author']}]: ").strip()

        if not new_author:
            break
        elif not is_letter_only(new_author, 20):
            print('Invalid author.')

        else:
            book['author'] = new_author
            break


    while True:
        new_price= input(f"New price [{book['price']}]: ").strip()

        if not new_price:
            break
        if not re.fullmatch(r'\d+(\.\d{2})', new_price):
            print('Invalid price')
        else:
            book['price'] = new_price
            break
        
        
    while True:
        new_copies = input(f"New copies [{book['copies']}]: ").strip()

        if not new_copies:
            break

        if not new_copies.isdigit() or int(new_copies) > 2:
            print('Invalid copies count.')
        else:
            copies = int(new_copies)
            issued_count = int(book['copies']) - int(book['availability'])

            if copies < issued_count:
                print('Copies cannot be less than currently issued count.')
            else:
                book['copies'] = str(copies)
                book['availability'] = str(copies - issued_count)
                break


    write_csv(BOOK_FILE, books, ['book_id', 'title', 'isbn13', 'author' , 'copies', 'availability', 'price'])
    print('Book updated successfully.')


def add_student():
    students = read_csv(STUDENT_FILE)

    while True:
        student_id = input('Enter student id (8 digits): ').strip()

        if not is_valid_student_id(student_id):
            print('Invalid student id.')
        elif find_row(students, 'student_id', student_id):
            print('student id already exists')
        else:
            break

    while True:
            
            first_name = input('Enter first name (letters only, max 10): ').strip()

            if not is_letter_only(first_name, 10):
                print('Invalid first name.')
            else:
                break

    students.append({'student_id': student_id, 'first_name': first_name})
    write_csv(STUDENT_FILE, students, ['student_id', 'first_name'])
    print('Student added successfully')



def issue_book():
    books = read_csv(BOOK_FILE)
    students = read_csv(STUDENT_FILE)

    while True:
        book_id = input('Enter book id: ').strip().upper()
        book = find_row(books, 'book_id', book_id)

        if not book:
            print('Invalid book id.')
        else:
            break

    while True:
        student_id = input('Enter student id:').strip()
        student = find_row(students, 'student_id', student_id)

        if not student:
            print('Invalid student id')
        else:
            break

    while True:
            return_date = input('Enter return date (DD/MM/YYYY):').strip()

            if not is_valid_date(return_date):
                print("Invalid date.")
            else:
                break

    if not current_issue_exists(book_id,student_id):
        print('Book already returned.')
        return
    
    new_availability = int(book['availability']) + 1
    
    if new_availability > int(book['availability']):
        print('Availability cannot exceed copies.')
        return
    
    book['availability'] = str(new_availability)

    write_csv(
        BOOK_FILE,
        books,
        ['book_id', 'time', 'isbn13', 'author', 'copies', 'availability', 'price']
    )

    append_csv(
        TRANSACTION_FILE,
        {
            'date': return_date,
            'book_id': book_id,
            'student_id': student_id,
            'type': '2'
        },
        ['date', 'book_id', 'student_id', 'type']
    )

    print('Book returned successfully.')


def return_book():
    books = read_csv(BOOK_FILE)
    students = read_csv(STUDENT_FILE)

    while True:
        book_id  = input('Enter book id: ').strip().upper()
        book = find_row(books, 'book_id', book_id)

        if not book:
            print('Invalid book id.')
        else:
            break

    while True:
        student_id = input('Enter student id: ').strip()
        student = find_row(students, 'student_id', student_id)

        if not student:
            print('Invalid student id.')
        else:
            break

    if not current_issue_exists(book_id, student_id):
        print('Book already returned.')
        return
    
    new_availability = int(book['availability']) + 1

    if new_availability > int(book['copies']):
        print('Availability cannot exceed copies.')
        return
    
    book['availability'] = str(new_availability)

    write_csv(
        BOOK_FILE,
        books,
        ['book_id', 'title', 'isbn13', 'author', 'copies', 'availability', 'price']
    )

    append_csv(
        TRANSACTION_FILE,
        {
            'date': return_date,
            'book_id': book_id,
            'student_id': student_id,
            'type': '2'
        },
        ['date', 'book_id', 'student_id', 'type']
    )

    print('Book returned successfully.')


def trend_graph():
    try:
        import pandas as pd
        import plotly.express as px
    except ImportError:
        print('Install pandas and plotly first.')
        return

    rows = read_csv(TRANSACTION_FILE)
    issue_rows = [row for row in rows if row['type'] == '1']
    if not issue_rows:
        print('No issue transactions found.')
        return
    counts = Counter(row['date'] for row in issue_rows)
    df = pd.DataFrame(sorted(counts.items()), columns=['date', 'issued_books'])
    fig = px.line(df, x='date', y='issued_books', title='Books Issued Trend by Date', markers=True)
    fig.show()
    

def main_menu():
    ensure_files()
    while True:
        # Optional: clear screen for clean UI
        os.system('cls' if os.name == 'nt' else 'clear')

        print('\nLibrary Management System - Python CLI\n')

        menu = [
            "1. Add book",
            "2. Edit book",
            "3. Search books",
            "4. View books",
            "5. Add student",
            "6. Issue book",
            "7. Return book",
            "8. Trend graph",
            "9. Exit"
        ]

        logo = [
            " _       __  __   _____",
            "| |     |  \\/  | / ____|",
            "| |     | \\  / || (___",
            "| |     | |\\/| | \\___ \\",
            "| |____ | |  | | ____) |",
            "|______||_|  |_||_____/",
            "",
            "",
            ""
        ]

        for i in range(len(menu)):
            print(f"{menu[i]:<35} {logo[i]}")

        choice = input('\nEnter choice: ').strip()

        if choice == '1':
            add_book()
        elif choice == '2':
            edit_book()
        elif choice == '3':
            search_books()
        elif choice == '4':
            view_books()
        elif choice == '5':
            add_student()
        elif choice == '6':
            issue_book()
        elif choice == '7':
            return_book()
        elif choice == '8':
            trend_graph()
        elif choice == '9':
            print('Goodbye, See you again.')
            break
        else:
            print('Invalid menu choice.')


if __name__ == '__main__':
    main_menu()