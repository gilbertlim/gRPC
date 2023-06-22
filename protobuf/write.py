import address_pb2

person = address_pb2.Person()
person.name = 'Gilbert'
person.age = 30
person.email = 'gilbert.app@gmail.com'

try:
  f = open('myaddress','wb')
  print(person.SerializeToString())
  f.write(person.SerializeToString()) # 직렬화
  f.close()
  print('file is wriiten')
except IOError:
  print('file creation error')