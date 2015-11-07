#!/usr/bin/python3

from random import randint

output = ""
filename = "data"


class Generator:

    def gen_date(self):
        return str(randint(2013, 2015)) + "-" \
            + str(randint(1, 12)) + "-" \
            + str(randint(1, 31))

    def gen_price(self):
        return str(10 * randint(10, 100))

    def gen_author(self):
        users = [
            "Intey",
            "Andrey",
            "Tatiana",
            "Nigger",
        ]
        return users[randint(1, len(users)-1)]

    def gen_parts(self):
        return str(randint(0, 15))

    def gen_row(self, s):
        return ":".join([s,
                         self.gen_price(),
                         self.gen_author(),
                         self.gen_date(),
                         self.gen_parts()]) + '\n'


def prepare_file(file_name):
    gena = Generator()
    with open(file_name, 'r') as f:
        file_lines = []
        for x in f.readlines():
            new_line = gena.gen_row(x.rstrip('\n'))
            # print(new_line)
            file_lines.append(new_line)

    # file_lines.sort(key=lambda line: int(line.split(":")[-1]))

    with open(file_name, 'w') as f:
        f.writelines(file_lines)


if __name__ == "__main__":
    prepare_file(filename)
