import address_pb2

person = address_pb2.Person()

try:
  f = open('myaddress','rb')
  person.ParseFromString(f.read())
  f.close()

  print(person.name)
  print(person.age)
  print(person.email)

except IOError:
  print('file read error')